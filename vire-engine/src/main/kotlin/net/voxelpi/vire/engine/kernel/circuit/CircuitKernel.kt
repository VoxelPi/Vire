package net.voxelpi.vire.engine.kernel.circuit

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.circuit.CircuitInstance
import net.voxelpi.vire.engine.circuit.CircuitState
import net.voxelpi.vire.engine.circuit.MutableCircuitInstanceImpl
import net.voxelpi.vire.engine.circuit.component.ComponentConfiguration
import net.voxelpi.vire.engine.circuit.emptyCircuitInstance
import net.voxelpi.vire.engine.circuit.emptyCircuitState
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelImpl
import net.voxelpi.vire.engine.kernel.KernelInstanceConfig
import net.voxelpi.vire.engine.kernel.KernelInstanceImpl
import net.voxelpi.vire.engine.kernel.KernelVariantConfig
import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.MutableKernelState
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.FieldInitializationContextImpl
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.OutputVectorElement
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.field
import net.voxelpi.vire.engine.kernel.variable.setting
import net.voxelpi.vire.engine.kernel.variable.storage.MutableFieldStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableOutputStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.mutableFieldStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.mutableOutputStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.vectorSizeStorage

public interface CircuitKernel : Kernel {

    public val circuit: Circuit

    public companion object {
        public val CIRCUIT_INSTANCE: Setting<CircuitInstance> = setting("__instance__", initialization = { emptyCircuitInstance() })
        public val CIRCUIT_STATE: Field<CircuitState> = field("__state__", initialization = { emptyCircuitState() })
    }
}

internal class CircuitKernelImpl(
    override val circuit: CircuitImpl,
) : KernelImpl(circuit.id, circuit.tags.toSet(), circuit.properties.toMap()), CircuitKernel {

    override val variables: Map<String, Variable<*>>

    init {
        val variables: MutableMap<String, Variable<*>> = circuit.variables().associateBy { it.name }.toMutableMap()
        variables[CircuitKernel.CIRCUIT_INSTANCE.name] = CircuitKernel.CIRCUIT_INSTANCE
        variables[CircuitKernel.CIRCUIT_STATE.name] = CircuitKernel.CIRCUIT_STATE
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
        val circuitKernelVariant = config.kernelVariant
        val settingStateStorage = config.settingStateStorage.mutableCopy()

        // Create instances for all component kernels.
        val circuitInstance = MutableCircuitInstanceImpl(mutableMapOf())
        for (component in circuit.components()) {
            // Build setting states for the kernel of the component.
            val kernelVariant = component.kernelVariant
            val settingStates: SettingStateMap = component.configuration.settingEntries.map { (settingName, value) ->
                settingName to when (value) {
                    is ComponentConfiguration.Entry.CircuitSetting -> config[value.setting]
                    is ComponentConfiguration.Entry.Value -> value.value
                }
            }.toMap()

            // Create the instance of the component kernel.
            val kernelInstance = kernelVariant.createInstance(settingStates).getOrElse {
                return Result.failure(it)
            }
            circuitInstance[component] = kernelInstance
        }
        settingStateStorage[CircuitKernel.CIRCUIT_INSTANCE] = circuitInstance

        // Generate initial field states.
        val fieldInitializationContext = FieldInitializationContextImpl(circuitKernelVariant, config.settingStateStorage)
        val fieldStateStorage: MutableFieldStateStorage = mutableFieldStateStorage(
            circuitKernelVariant,
            circuitKernelVariant.fields().associate { it.name to (it.initialization(fieldInitializationContext)) },
        )

        // Generate initial output states.
        val outputStateStorage: MutableOutputStateStorage = mutableOutputStateStorage(
            circuitKernelVariant,
            circuitKernelVariant.outputs().associate {
                it.name to Array(if (it is OutputVector) circuitKernelVariant.size(it) else 1) { LogicState.EMPTY }
            },
        )

        // Create the instance.
        val instance = KernelInstanceImpl(
            config.kernelVariant,
            settingStateStorage,
            fieldStateStorage.copy(),
            outputStateStorage.copy(),
        )
        return Result.success(instance)
    }

    override fun updateKernel(state: MutableKernelState) {
        // Create a copy of the previous circuit state.
        val circuitState = state[CircuitKernel.CIRCUIT_STATE].mutableClone()

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
        state[CircuitKernel.CIRCUIT_STATE] = circuitState
    }
}
