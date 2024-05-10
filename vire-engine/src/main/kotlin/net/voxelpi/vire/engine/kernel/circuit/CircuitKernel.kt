package net.voxelpi.vire.engine.kernel.circuit

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.circuit.CircuitState
import net.voxelpi.vire.engine.circuit.MutableCircuitStateImpl
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelImpl
import net.voxelpi.vire.engine.kernel.KernelVariantConfig
import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.MutableKernelState
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVectorElement
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.field

public interface CircuitKernel : Kernel {

    public val circuit: Circuit
}

internal class CircuitKernelImpl(
    id: Identifier,
    tags: Set<Identifier>,
    properties: Map<Identifier, String>,
    override val circuit: CircuitImpl,
) : KernelImpl(id, tags, properties), CircuitKernel {

    override val variables: Map<String, Variable<*>>

    init {
        val variables: MutableMap<String, Variable<*>> = circuit.variables().associateBy { it.name }.toMutableMap()
        variables[CIRCUIT_STATE_FIELD.name] = CIRCUIT_STATE_FIELD
        this.variables = variables
    }

    override fun generateVariant(config: KernelVariantConfig): Result<KernelVariantImpl> {
        val variant = KernelVariantImpl(this, config.variableStates, emptyMap())
        return Result.success(variant)
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
        private val CIRCUIT_STATE_FIELD = field<CircuitState>("state", initialization = { MutableCircuitStateImpl() })
    }
}
