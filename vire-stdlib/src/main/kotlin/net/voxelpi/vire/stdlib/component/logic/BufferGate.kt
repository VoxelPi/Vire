package net.voxelpi.vire.stdlib.component.logic

import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.stdlib.VireStandardLibrary

object BufferGate : StateMachine(VireStandardLibrary, "buffer") {

    val input = declareInput("input", 1)
    val output = declareOutput("output", 1)

    override fun tick(context: StateMachineContext) {
        context[output] = context[input]
    }
}
