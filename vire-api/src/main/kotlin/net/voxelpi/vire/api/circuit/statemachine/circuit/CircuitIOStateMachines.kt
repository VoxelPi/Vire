package net.voxelpi.vire.api.circuit.statemachine.circuit

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.StateMachineProvider
import net.voxelpi.vire.api.circuit.statemachine.input
import net.voxelpi.vire.api.circuit.statemachine.output
import net.voxelpi.vire.api.circuit.statemachine.parameter
import net.voxelpi.vire.api.circuit.statemachine.variable

object Input : StateMachineProvider {
    val name = parameter("name", "input")
    val value = variable("value", LogicState.EMPTY)
    val output = output("output")

    /**
     * Tag for state machines that should act as circuit input when creating circuit-state-machines.
     */
    val CIRCUIT_INPUT_TAG = Identifier.parse("vire:circuit_input")

    override val stateMachine = StateMachine.create(Identifier("vire", "input")) {
        tags += CIRCUIT_INPUT_TAG
        declare(name)
        declare(value)
        declare(output)

        update = { context ->
            context[output] = context[value]
        }
    }
}

object Output : StateMachineProvider {
    val name = parameter("name", "output")
    val value = variable("value", LogicState.EMPTY)
    val input = input("input")

    /**
     * Tag for state machines that should act as circuit output when creating circuit-state-machines.
     */
    val CIRCUIT_OUTPUT_TAG = Identifier.parse("vire:circuit_output")

    override val stateMachine = StateMachine.create(Identifier("vire", "output")) {
        tags += CIRCUIT_OUTPUT_TAG
        declare(name)
        declare(value)
        declare(input)

        update = { context ->
            context[value] = context[input]
        }
    }
}
