package net.voxelpi.vire.engine.kernel.circuit

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.CircuitInstance
import net.voxelpi.vire.engine.circuit.CircuitState
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.registered.RegisteredKernel
import net.voxelpi.vire.engine.kernel.registered.registeredKernel
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.createField
import net.voxelpi.vire.engine.kernel.variable.createParameter

public object CircuitKernel : KernelProvider {

    public val ID: Identifier = Identifier("vire", "circuit")

    public val CIRCUIT_KERNEL_TAG: Identifier = Identifier("vire", "circuit")

    public val CIRCUIT: Parameter<Circuit> = createParameter("circuit")

    public val CIRCUIT_INSTANCE: Field<CircuitInstance> = createField("instance")

    public val CIRCUIT_STATE: Field<CircuitState> = createField("state")

    public fun createVariant(circuit: Circuit): KernelVariant {
        return kernel.createVariant {
            this[CIRCUIT] = circuit
        }.getOrThrow()
    }

    public override val kernel: RegisteredKernel = registeredKernel(ID) {
        tags += CIRCUIT_KERNEL_TAG

        declare(CIRCUIT)
        declare(CIRCUIT_INSTANCE)
        declare(CIRCUIT_STATE)

        onConfiguration { context ->
            val circuit = context[CIRCUIT]

            // Update the size of all vector variables.
            for (vector in circuit.variables().filterIsInstance<VectorVariable<*>>()) {
                context.resize(vector, circuit.size(vector))
            }
        }

        onInitialization { context ->
            val circuit = context[CIRCUIT]

            // Initialize the circuit instance.
            val circuitInstance = circuit.createCircuitInstance(context).getOrElse {
                context.signalInvalidConfiguration(it.message ?: "")
            }
            context[CIRCUIT_INSTANCE] = circuitInstance

            // Initialize the circuit state.
            val circuitState = circuitInstance.createInitialState()
            context[CIRCUIT_STATE] = circuitState
        }

        onUpdate { context ->
            val circuitState = context[CIRCUIT_STATE].mutableClone()

            circuitState.updateCircuitInputs(context)
            circuitState.updateCircuit()
            circuitState.updateCircuitOutputs(context)

            context[CIRCUIT_STATE] = circuitState
        }
    }
}
