package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.stdlib.VireStandardLibrary

object FullAdder : StateMachine(VireStandardLibrary, "full_adder") {

    val inputA = declareInput("input_a")
    val inputB = declareInput("input_b")
    val carryIn = declareInput("carry_in")
    val sum = declareOutput("output")
    val carryOut = declareOutput("carry_out")

    override fun tick(context: StateMachineContext) {
        val a = context[inputA]
        val b = context[inputB]
        val c = context[carryIn]

        context[sum] = a xor b xor c
        context[carryOut] = (a and b) or (a and c) or (b and c)
    }
}
