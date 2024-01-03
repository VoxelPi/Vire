package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.stdlib.VireStandardLibrary

val HalfAdder = StateMachine.create(Identifier(VireStandardLibrary.id, "half_adder")) {
    val inputA = declareInput("input_a")
    val inputB = declareInput("input_b")
    val sum = declareOutput("output")
    val carryOut = declareOutput("carry_out")

    update = { context ->
        val a = context[inputA].booleanState()
        val b = context[inputB].booleanState()

        context[sum] = a xor b
        context[carryOut] = a and b
    }
}

val FullAdder = StateMachine.create(Identifier(VireStandardLibrary.id, "full_adder")) {

    val inputA = declareInput("input_a")
    val inputB = declareInput("input_b")
    val carryIn = declareInput("carry_in")
    val sum = declareOutput("output")
    val carryOut = declareOutput("carry_out")

    update = { context ->
        val a = context[inputA].booleanState()
        val b = context[inputB].booleanState()
        val c = context[carryIn].booleanState()

        context[sum] = a xor b xor c
        context[carryOut] = (a and b) or (a and c) or (b and c)
    }
}
