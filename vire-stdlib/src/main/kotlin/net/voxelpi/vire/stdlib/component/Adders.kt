package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineProvider
import net.voxelpi.vire.api.simulation.statemachine.input
import net.voxelpi.vire.api.simulation.statemachine.output
import net.voxelpi.vire.stdlib.VIRE_STDLIB_ID

object HalfAdder : StateMachineProvider {
    val inputA = input("input_a")
    val inputB = input("input_b")
    val sum = output("output")
    val carryOut = output("carry_out")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "half_adder")) {
        declare(inputA)
        declare(inputB)
        declare(sum)
        declare(carryOut)

        update = { context ->
            val a = context[inputA].booleanState()
            val b = context[inputB].booleanState()

            context[sum] = a xor b
            context[carryOut] = a and b
        }
    }
}

object FullAdder : StateMachineProvider {
    val inputA = input("input_a")
    val inputB = input("input_b")
    val carryIn = input("carry_in")
    val sum = output("output")
    val carryOut = output("carry_out")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "full_adder")) {
        declare(inputA)
        declare(inputB)
        declare(carryIn)
        declare(sum)
        declare(carryOut)

        update = { context ->
            val a = context[inputA].booleanState()
            val b = context[inputB].booleanState()
            val c = context[carryIn].booleanState()

            context[sum] = a xor b xor c
            context[carryOut] = (a and b) or (a and c) or (b and c)
        }
    }
}
