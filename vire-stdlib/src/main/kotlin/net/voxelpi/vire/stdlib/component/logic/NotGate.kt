package net.voxelpi.vire.stdlib.component.logic

import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.stdlib.VireStandardLibrary

object NotGate : StateMachine(VireStandardLibrary, "not") {

    val input = declareInput("input")
    val output = declareOutput("output")

    override fun tick(context: StateMachineContext) {
        context[output] = !context[input]
    }
}
