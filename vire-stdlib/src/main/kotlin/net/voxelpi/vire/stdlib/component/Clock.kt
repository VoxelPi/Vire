package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineProvider
import net.voxelpi.vire.api.simulation.statemachine.output
import net.voxelpi.vire.api.simulation.statemachine.parameter
import net.voxelpi.vire.api.simulation.statemachine.variable
import net.voxelpi.vire.stdlib.VIRE_STDLIB_ID

object Clock : StateMachineProvider {
    val ticksHigh = parameter("ticks_high", 1L, min = 1L)
    val ticksLow = parameter("ticks_low", 1L, min = 1L)
    val ticks = variable("ticks", 0L)
    val output = output("output")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "clock")) {
        declare(ticksHigh)
        declare(ticksLow)
        declare(ticks)
        declare(output)

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
}
