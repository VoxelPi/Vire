package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.stdlib.VireStandardLibrary

val Clock = StateMachine.create(Identifier(VireStandardLibrary.id, "clock")) {

    val ticksHigh = declareParameter("ticks_high", 1L, min = 1L)
    val ticksLow = declareParameter("ticks_low", 1L, min = 1L)
    val ticks = declareVariable("ticks", 0L)
    val output = declareOutput("output")

    update = { context ->
        // Update output
        context[output] = LogicState.value(context[ticks] < context[ticksHigh])

        // Increment counter
        context[ticks] = context[ticks] + 1
        if (context[ticks] >= context[ticksHigh] + context[ticksLow]) {
            context[ticks] = 0
        }
    }
}
