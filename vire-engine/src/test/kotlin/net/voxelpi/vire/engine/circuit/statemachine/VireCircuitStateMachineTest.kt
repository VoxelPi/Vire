package net.voxelpi.vire.engine.circuit.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.circuit.Input
import net.voxelpi.vire.api.circuit.statemachine.circuit.Output
import net.voxelpi.vire.api.circuit.statemachine.input
import net.voxelpi.vire.api.circuit.statemachine.output
import net.voxelpi.vire.engine.VireImplementation
import net.voxelpi.vire.engine.circuit.VireCircuit
import net.voxelpi.vire.engine.environment.VireEnvironment
import net.voxelpi.vire.engine.simulation.VireSimulation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VireCircuitStateMachineTest {

    private lateinit var environment: VireEnvironment
    private lateinit var circuit: VireCircuit
    private lateinit var simulation: VireSimulation

    @BeforeEach
    fun setUp() {
        environment = VireImplementation.createEnvironment(emptyList())
        circuit = environment.createCircuit()
        simulation = environment.createSimulation(circuit)
    }

    @Test
    fun `test circuit state machines`() {
        val integratedCircuit = environment.createCircuit()

        val internalInputVariable = input("input")
        val internalOutputVariable = output("output")
        val internalStateMachine = StateMachine.create(Identifier("vire-test", "buffer")) {
            declare(internalInputVariable)
            declare(internalOutputVariable)

            update = { context ->
                context[internalOutputVariable] = context[internalInputVariable].booleanState()
            }
        }

        val internalComponent = integratedCircuit.createComponent(internalStateMachine)
        val internalComponentInputPort = internalComponent.createPort(internalInputVariable.variable(0))
        val internalComponentOutputPort = internalComponent.createPort(internalOutputVariable.variable(0))

        // Create an input component in the integrated circuit.
        val integratedInputComponent = integratedCircuit.createComponent(Input.stateMachine) {
            this[Input.name] = "integrated_input"
        }
        val integratedInput = integratedInputComponent.createPort(Input.output.variable(0))

        // Create an output component in the integrated circuit.
        val integratedOutputComponent = integratedCircuit.createComponent(Output.stateMachine) {
            this[Output.name] = "integrated_output"
        }
        val integratedOutput = integratedOutputComponent.createPort(Output.input.variable(0))

        // Connect the internal networks.
        integratedCircuit.createNetworkNodeConnection(integratedInput.node, internalComponentInputPort.node)
        integratedCircuit.createNetworkNodeConnection(integratedOutput.node, internalComponentOutputPort.node)

        val stateMachine = StateMachine.createFromCircuit(Identifier.parse("test:internal"), integratedCircuit)
        require(stateMachine is VireStateMachine)
        val input = stateMachine.inputs["integrated_input"]!!
        val output = stateMachine.outputs["integrated_output"]!!
        val instance = stateMachine.createInstance {}

        instance[input] = LogicState.value(true, 2)
        instance.initializeOutputs()
        instance.update()

        val result = instance[output]
        assertEquals(LogicState.value(true, 2), result)
    }
}
