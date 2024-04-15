package net.voxelpi.vire.engine.circuit.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.LogicValue
import net.voxelpi.vire.api.circuit.statemachine.StateMachineParameter
import net.voxelpi.vire.api.circuit.statemachine.annotation.InitialParameterSize
import net.voxelpi.vire.api.circuit.statemachine.annotation.Input
import net.voxelpi.vire.api.circuit.statemachine.annotation.IntLimits
import net.voxelpi.vire.api.circuit.statemachine.annotation.Output
import net.voxelpi.vire.api.circuit.statemachine.annotation.Parameter
import net.voxelpi.vire.api.circuit.statemachine.annotation.StateMachineMeta
import net.voxelpi.vire.api.circuit.statemachine.annotation.StateMachineTemplate
import net.voxelpi.vire.api.circuit.statemachine.annotation.Tagged
import net.voxelpi.vire.api.circuit.statemachine.annotation.Variable
import net.voxelpi.vire.engine.VireImplementation
import net.voxelpi.vire.engine.simulation.VireSimulation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VireStateMachineFactoryTest {

    private lateinit var simulation: VireSimulation

    @BeforeEach
    fun setUp() {
        simulation = VireImplementation.createSimulation(emptyList())
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun createAnnotatedStateMachine() {
        val stateMachine = VireStateMachine.generate<Buffer>()
        assertEquals(setOf("counter_step", "size"), stateMachine.parameters.keys)
        assertEquals(setOf("counter"), stateMachine.variables.keys)
        assertEquals(setOf("input", "inputs"), stateMachine.inputs.keys)
        assertEquals(setOf("output", "active", "outputs"), stateMachine.outputs.keys)

        // Check tags.
        assertTrue(Identifier.parse("vire:test1") in stateMachine.tags)
        assertTrue(Identifier.parse("vire:test2") !in stateMachine.tags)
        assertTrue(Identifier.parse("vire:test3") in stateMachine.tags)
        assertTrue(Identifier.parse("vire:test4") in stateMachine.tags)
        assertEquals(3, stateMachine.tags.size)

        val stateMachineInstance1 = stateMachine.createInstance {
            this[stateMachine.parameters["size"]!! as StateMachineParameter<Int>] = 3
        }
        val stateMachineInstance2 = stateMachine.createInstance {
            this[stateMachine.parameters["counter_step"]!! as StateMachineParameter<Int>] = 2
        }

        // Test initial values.
        assertEquals(LogicState.value(LogicValue.TRUE, 1), stateMachineInstance1["active"])
        assertEquals(0, stateMachineInstance1["counter"])

        // Test modifying the state machine instance.
        val state1 = LogicState.value(LogicValue.TRUE, 2)
        val states1 = arrayOf(LogicState.value(false, 3), LogicState.value(true, 5), LogicState.value(null, 2))
        stateMachineInstance1["input"] = state1
        stateMachineInstance1.vector("inputs", states1)
        stateMachineInstance1.update()
        assertEquals(state1, stateMachineInstance1["output"])
        assertEquals(states1, stateMachineInstance1.vector("outputs"))
        assertEquals(1, stateMachineInstance1["counter"])
        assertEquals(0, stateMachineInstance2["counter"])

        // Test modifying the other state machine instance.
        val state2 = LogicState.value(LogicValue.FALSE, 3)
        stateMachineInstance2["input"] = state2
        stateMachineInstance2.update()
        assertEquals(state2, stateMachineInstance2["output"])
        assertEquals(2, stateMachineInstance2["counter"])

        // Check that the other instance did not change.
        assertEquals(state1, stateMachineInstance1["output"])
        assertEquals(state2, stateMachineInstance2["output"])
    }

    @StateMachineMeta("test", "buffer")
    @Tagged("vire:test1", "vire:test3")
    @Tagged("vire:test3", "vire:test4")
    class Buffer : StateMachineTemplate {

        @Parameter("counter_step")
        @IntLimits(min = 1)
        var counterStep: Int = 1

        @Parameter("size")
        @IntLimits(min = 1)
        var size: Int = 1

        @Input("input")
        lateinit var input: LogicState

        @Input("inputs")
        @InitialParameterSize("size")
        lateinit var inputs: Array<LogicState>

        @Output("output")
        lateinit var output: LogicState

        @Output("outputs")
        @InitialParameterSize("size")
        lateinit var outputs: Array<LogicState>

        @Output("active")
        val active: LogicState = LogicState.value(LogicValue.TRUE, 1)

        @Variable("counter")
        var counter: Int = 0

        override fun update() {
            output = input
            outputs = inputs
            counter += counterStep
        }
    }
}
