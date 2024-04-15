package net.voxelpi.vire.stdlib.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.LogicValue
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.StateMachineProvider
import net.voxelpi.vire.api.circuit.statemachine.input
import net.voxelpi.vire.api.circuit.statemachine.output
import net.voxelpi.vire.api.circuit.statemachine.parameter
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
