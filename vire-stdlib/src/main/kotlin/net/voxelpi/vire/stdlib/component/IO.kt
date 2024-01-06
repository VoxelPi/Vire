package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.LogicValue
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineProvider
import net.voxelpi.vire.api.simulation.statemachine.input
import net.voxelpi.vire.api.simulation.statemachine.output
import net.voxelpi.vire.api.simulation.statemachine.parameter
import net.voxelpi.vire.stdlib.VIRE_STDLIB_ID

object Input : StateMachineProvider {
    val value = parameter("value", LogicValue.NONE)
    val channels = parameter("channels", 1, min = 1)
    val output = output("output")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "input")) {
        declare(value)
        declare(channels)
        declare(output)

        configure = { context ->
            context[output] = LogicState.value(context[value], context[channels])
        }
    }
}

object Output : StateMachineProvider {
    val input = input("input")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "output")) {
        declare(input)
    }
}
