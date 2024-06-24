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
            val circuitInstance = MutableCircuitInstanceImpl(circuit, mutableMapOf())
            circuitInstance.initialize(context).getOrElse {
                context.signalInvalidConfiguration(it.message ?: "")
            }
            context[CircuitKernel.CIRCUIT_INSTANCE] = circuitInstance

            // Initialize the circuit state.
            val circuitState = MutableCircuitStateImpl(circuitInstance, mutableMapOf(), mutableMapOf())
            circuitState.initialize(circuit, circuitInstance).getOrElse {
                context.signalInvalidConfiguration(it.message ?: "")
            }
            circuitState.updateComponentOutputs()
            circuitState.updateCircuitOutputs(context)
            context[CircuitKernel.CIRCUIT_STATE] = circuitState
        }

        onUpdate { context ->
            val circuitState = context[CircuitKernel.CIRCUIT_STATE]!!.mutableClone() as MutableCircuitStateImpl

            circuitState.updateCircuitInputs(context)
            circuitState.updateComponentInputs()
            circuitState.updateComponents()
            circuitState.updateComponentOutputs()
            circuitState.updateCircuitOutputs(context)

            context[CircuitKernel.CIRCUIT_STATE] = circuitState
        }
    }
}

public object CircuitKernel {

    public val CIRCUIT_INSTANCE: Field<CircuitInstance?> = createField("__instance__") {
        initialization = { null }
    }

    public val CIRCUIT_STATE: Field<CircuitState?> = createField("__state__") {
        initialization = { null }
    }
}
