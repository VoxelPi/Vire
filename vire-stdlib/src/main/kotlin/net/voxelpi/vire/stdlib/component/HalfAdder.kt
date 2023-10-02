package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.stdlib.VireStandardLibrary

object HalfAdder : StateMachine(VireStandardLibrary, "half_adder") {

    val inputA = declareInput("input_a")
    val inputB = declareInput("input_b")
    val sum = declareOutput("output")
    val carryOut = declareOutput("carry_out")

    override fun tick(context: StateMachineContext) {
        val a = context[inputA]
        val b = context[inputB]

        context[sum] = a xor b
        context[carryOut] = a and b
    }
}
