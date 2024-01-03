package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.LogicValue
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.annotation.Input
import net.voxelpi.vire.api.simulation.statemachine.annotation.Output
import net.voxelpi.vire.api.simulation.statemachine.annotation.StateMachineMeta
import net.voxelpi.vire.api.simulation.statemachine.annotation.StateMachineTemplate
import net.voxelpi.vire.engine.VireImplementation
import net.voxelpi.vire.engine.simulation.VireSimulation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class VireStateMachineFactoryTest {

    private lateinit var simulation: VireSimulation

    @BeforeEach
    fun setUp() {
        simulation = VireImplementation.createSimulation(emptyList())
    }

    @Test
    fun createAnnotatedStateMachine() {
        val stateMachine = StateMachine.create<Buffer>()
        val stateMachineInstance1 = simulation.createStateMachineInstance(stateMachine)
        val stateMachineInstance2 = simulation.createStateMachineInstance(stateMachine)

        // Test initial values.
        assertEquals(LogicState.value(LogicValue.TRUE, 1), stateMachineInstance1[stateMachine.outputs["active"]!!])

        // Test modifying the state machine instance.
        val state1 = LogicState.value(LogicValue.TRUE, 2)
        stateMachineInstance1[stateMachine.inputs["input"]!!] = state1
        stateMachineInstance1.update()
        assertEquals(state1, stateMachineInstance1[stateMachine.outputs["output"]!!])

        // Test modifying the other state machine instance.
        val state2 = LogicState.value(LogicValue.FALSE, 3)
        stateMachineInstance2[stateMachine.inputs["input"]!!] = state2
        stateMachineInstance2.update()
        assertEquals(state2, stateMachineInstance2[stateMachine.outputs["output"]!!])

        // Check that the other instance did not change.
        assertEquals(state1, stateMachineInstance1[stateMachine.outputs["output"]!!])
        assertEquals(state2, stateMachineInstance2[stateMachine.outputs["output"]!!])
    }

    @StateMachineMeta("test", "buffer")
    class Buffer : StateMachineTemplate {

        @Input("input")
        lateinit var input: LogicState

        @Output("output")
        lateinit var output: LogicState

        @Output("active")
        val active: LogicState = LogicState.value(LogicValue.TRUE, 1)

        override fun update() {
            output = input
        }
    }
}
