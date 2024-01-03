package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.statemachine.annotation.Input
import net.voxelpi.vire.api.simulation.statemachine.annotation.StateMachineMeta
import net.voxelpi.vire.api.simulation.statemachine.annotation.StateMachineTemplate

@StateMachineMeta("vire", "test")
class TestComponent : StateMachineTemplate {

    @Input("input1")
    lateinit var input1: LogicState

    @Input("output1")
    lateinit var output1: LogicState
}
