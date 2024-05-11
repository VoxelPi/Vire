package net.voxelpi.vire.engine.kernel.circuit

import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.circuit.CircuitState
import net.voxelpi.vire.engine.circuit.MutableCircuitInstanceImpl
import net.voxelpi.vire.engine.circuit.MutableCircuitStateImpl
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelImpl
import net.voxelpi.vire.engine.kernel.KernelInstanceConfig
import net.voxelpi.vire.engine.kernel.KernelInstanceImpl
import net.voxelpi.vire.engine.kernel.KernelVariantConfig
import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.MutableKernelState
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVectorElement
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.field
import net.voxelpi.vire.engine.kernel.variable.setting
import net.voxelpi.vire.engine.kernel.variable.storage.fieldStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.outputStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.vectorSizeStorage

public interface CircuitKernel : Kernel {

    public val circuit: Circuit
}

internal class CircuitKernelImpl(
    override val circuit: CircuitImpl,
) : KernelImpl(circuit.id, circuit.tags.toSet(), circuit.properties.toMap()), CircuitKernel {

    override val variables: Map<String, Variable<*>>

    init {
        val variables: MutableMap<String, Variable<*>> = circuit.variables().associateBy { it.name }.toMutableMap()
        variables[CIRCUIT_INSTANCE_SETTING.name] = CIRCUIT_INSTANCE_SETTING
        variables[CIRCUIT_STATE_FIELD.name] = CIRCUIT_STATE_FIELD
        this.variables = variables
    }

    override fun generateVariant(config: KernelVariantConfig): Result<KernelVariantImpl> {
        val variant = KernelVariantImpl(
            this,
            variables,
            config.parameterStateStorage,
            vectorSizeStorage(config.kernel, emptyMap()),
        )
        return Result.success(variant)
    }

    override fun generateInstance(config: KernelInstanceConfig): Result<KernelInstanceImpl> {
        val settingStateStorage = config.settingStateStorage.mutableCopy()

        // Create instances for all component kernels.
        val circuitInstance = MutableCircuitInstanceImpl(mutableMapOf())
        for (component in circuit.components()) {
            val kernelVariant = component.kernelVariant
            val kernelInstance = kernelVariant.createInstance().getOrElse {
                return Result.failure(it)
            }
            circuitInstance[component] = kernelInstance
        }
        settingStateStorage[CIRCUIT_INSTANCE_SETTING] = circuitInstance

        // Create the instance.
        val instance = KernelInstanceImpl(
            config.kernelVariant,
            settingStateStorage,
            fieldStateStorage(config.kernelVariant, emptyMap()),
            outputStateStorage(config.kernelVariant, emptyMap()),
        )
        return Result.success(instance)
    }

    override fun updateKernel(state: MutableKernelState) {
        // Create a copy of the previous circuit state.
        val circuitState = state[CIRCUIT_STATE_FIELD].mutableClone()

        // Push all terminal inputs -> network states.
        // This is where the input variables of the circuit kernel are read.
        for (terminal in circuit.terminals()) {
            circuitState[terminal.network] = when (val variable = terminal.variable ?: continue) {
                is InputScalar -> state[variable]
                is InputVectorElement -> state[variable]
                is OutputScalar, is OutputVectorElement -> continue
            }
        }

        // Push all network states -> port inputs.
        for (component in circuit.components()) {
            val componentState = circuitState[component]
            for (port in component.ports()) {
                when (val variable = port.variable ?: continue) {
                    is InputScalar -> componentState[variable] = circuitState[port.network]
                    is InputVectorElement -> componentState[variable] = circuitState[port.network]
                    is OutputScalar, is OutputVectorElement -> continue
                }
            }
        }

        // Update the kernels of all components.
        for (component in circuit.components()) {
            val componentState = circuitState[component]
            component.kernel.updateKernel(componentState)
        }

        // Reset all network states.
        circuitState.resetNetworkStates()

        // Push all port outputs -> network states.
        for (component in circuit.components()) {
            val componentState = circuitState[component]
            for (port in component.ports()) {
                circuitState[port.network] = when (val variable = port.variable ?: continue) {
                    is OutputScalar -> componentState[variable]
                    is OutputVectorElement -> componentState[variable]
                    is InputScalar, is InputVectorElement -> continue
                }
            }
        }

        // Push all network states -> terminal outputs.
        // This is where the output variables of the circuit kernel are written.
        for (terminal in circuit.terminals()) {
            when (val variable = terminal.variable ?: continue) {
                is OutputScalar -> state[variable] = circuitState[terminal.network]
                is OutputVectorElement -> state[variable] = circuitState[terminal.network]
                is InputScalar, is InputVectorElement -> continue
            }
        }

        // Store the new circuit state.
        state[CIRCUIT_STATE_FIELD] = circuitState
    }

    companion object {
        private val CIRCUIT_INSTANCE_SETTING = setting("instance", initialization = { MutableCircuitInstanceImpl(mutableMapOf()) })
        private val CIRCUIT_STATE_FIELD = field<CircuitState>("state", initialization = { MutableCircuitStateImpl() })
    }
}
