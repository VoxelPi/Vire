package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.api.simulation.network.NetworkState
import net.voxelpi.vire.stdlib.VireStandardLibrary

object Clock : StateMachine(VireStandardLibrary, "clock") {

    val ticksHigh = declareParameter("ticks_high", 1L, min = 1L)
    val ticksLow = declareParameter("ticks_low", 1L, min = 1L)
    val ticks = declareVariable("ticks", 0L)
    val output = declareOutput("output", 1)

    override fun tick(context: StateMachineContext) {
        // Update output
        context[output] = NetworkState.value(context[ticks] < context[ticksHigh])

        // Increment counter
        context[ticks] = context[ticks] + 1
        if (context[ticks] >= context[ticksHigh] + context[ticksLow]) {
            context[ticks] = 0
        }
    }
}
