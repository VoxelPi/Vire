package net.voxelpi.vire.engine.kernel.circuit

import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.circuit.CircuitInstance
import net.voxelpi.vire.engine.circuit.CircuitState
import net.voxelpi.vire.engine.circuit.MutableCircuitInstanceImpl
import net.voxelpi.vire.engine.circuit.MutableCircuitStateImpl
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVectorElement
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.createField

/**
 * Creates a new [Kernel] from the given [circuit].
 */
public fun circuitKernel(circuit: Circuit): Kernel {
    require(circuit is CircuitImpl)

    return kernel {
        declare(CircuitKernel.CIRCUIT_INSTANCE)
        declare(CircuitKernel.CIRCUIT_STATE)
        for (variable in circuit.variables()) {
            declare(variable)
        }

        onConfiguration { context ->
            // Update the size of all vector variables.
            for (vector in circuit.variables().filterIsInstance<VectorVariable<*>>()) {
                context.resize(vector, circuit.size(vector))
            }
        }

        onInitialization { context ->
            // Initialize the circuit instance.
            val circuitInstance = context[CircuitKernel.CIRCUIT_INSTANCE] as MutableCircuitInstanceImpl
            circuitInstance.initialize(circuit, context).getOrElse {
                context.signalInvalidConfiguration(it.message ?: "")
            }

            // Initialize the circuit state.
            val circuitState = context[CircuitKernel.CIRCUIT_STATE] as MutableCircuitStateImpl
            circuitState.initialize(circuit, circuitInstance).getOrElse {
                context.signalInvalidConfiguration(it.message ?: "")
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
                    is OutputScalar -> context[variable] = circuitState[terminal.network]
                    is OutputVectorElement -> context[variable] = circuitState[terminal.network]
                    is InputScalar, is InputVectorElement -> continue
                }
            }
        }

        onUpdate { context ->
            // Create a copy of the previous circuit state.
            val circuitState = context[CircuitKernel.CIRCUIT_STATE] as MutableCircuitStateImpl

            // Push all terminal inputs -> network states.
            // This is where the input variables of the circuit kernel are read.
            for (terminal in circuit.terminals()) {
                circuitState[terminal.network] = when (val variable = terminal.variable ?: continue) {
                    is InputScalar -> context[variable]
                    is InputVectorElement -> context[variable]
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
                    is OutputScalar -> context[variable] = circuitState[terminal.network]
                    is OutputVectorElement -> context[variable] = circuitState[terminal.network]
                    is InputScalar, is InputVectorElement -> continue
                }
            }
        }
    }
}

public object CircuitKernel {

    public val CIRCUIT_INSTANCE: Field<CircuitInstance> = createField("__instance__") {
        initialization = { MutableCircuitInstanceImpl.createEmpty() }
    }

    public val CIRCUIT_STATE: Field<CircuitState> = createField("__state__") {
        initialization = { MutableCircuitStateImpl.createEmpty() }
    }
}
