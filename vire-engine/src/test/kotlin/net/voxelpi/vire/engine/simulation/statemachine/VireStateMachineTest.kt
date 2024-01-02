package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.input
import net.voxelpi.vire.api.simulation.statemachine.output
import net.voxelpi.vire.api.simulation.statemachine.parameter
import net.voxelpi.vire.engine.VireImplementation
import net.voxelpi.vire.engine.simulation.VireSimulation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class VireStateMachineTest {

    private lateinit var simulation: VireSimulation

    @BeforeEach
    fun setUp() {
        simulation = VireImplementation.createSimulation(emptyList())
    }

    @Test
    fun testParameterInitialSizeProvider() {
        val inputSize = parameter("input_size", 2, 2)
        val outputSize = parameter("output_size", 3, 3)
        val input = input("input", inputSize)
        val output = output("output", outputSize)

        // Create state machine.
        val stateMachine = StateMachine.create(Identifier("test", "test")) {
            declare(inputSize)
            declare(outputSize)
            declare(input)
            declare(output)
        }

        // Create state machine instance.
        val instance = simulation.createStateMachineInstance(stateMachine) {
            this[inputSize] = 5
            this[outputSize] = 3
        }

        // Check that the parameters have been set.
        assertEquals(5, instance.size(input))
        assertEquals(3, instance.size(output))
    }

    @Test
    fun testParameterConfigurationList() {
        val parameter = parameter("parameter", 5, 1, 10)

        // Create state machine.
        val stateMachine = StateMachine.create(Identifier("test", "test")) {
            declare(parameter)
        }

        // Check that specified parameter is used.
        val instance = simulation.createStateMachineInstance(stateMachine, mapOf("parameter" to 3))
        assertEquals(3, instance[parameter])

        // Check that an exception is thrown for unknown parameters.
        assertThrows<Exception> { simulation.createStateMachineInstance(stateMachine, mapOf("unknown" to "fail")) }
    }

    @Test
    fun testParameterRange() {
        val parameter = parameter("parameter", 5, 1, 10)

        // Create state machine.
        val stateMachine = StateMachine.create(Identifier("test", "test")) {
            declare(parameter)
        }

        // Check that an exception is thrown for values smaller than the specified minimum.
        assertThrows<Exception> {
            simulation.createStateMachineInstance(stateMachine) {
                this[parameter] = 0
            }
        }

        // Check that an exception is thrown for values greater than the specified maximum.
        assertThrows<Exception> {
            simulation.createStateMachineInstance(stateMachine) {
                this[parameter] = 20
            }
        }
    }

    @Test
    fun testParameterReconfiguration() {
        val parameter = parameter("parameter", 5, 1, 10)
        val input = input("input", initialSize = parameter)
        val output = output("output", initialSize = parameter)

        // Create state machine.
        val stateMachine = StateMachine.create(Identifier("test", "test")) {
            declare(parameter)
            declare(input)
            declare(output)
        }

        // Create an instance of the state machine.
        val instance = simulation.createStateMachineInstance(stateMachine)
        assertEquals(5, instance[parameter])
        assertEquals(5, instance.size(input))
        assertEquals(5, instance.size(output))

        instance.configureParameters {
            this[parameter] = 6
        }
        assertEquals(6, instance[parameter])
        assertEquals(6, instance.size(input))
        assertEquals(6, instance.size(output))
    }
}
