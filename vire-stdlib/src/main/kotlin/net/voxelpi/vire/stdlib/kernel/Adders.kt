package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.output

public object HalfAdder : KernelProvider {
    public val inputA: InputScalar = input("input_a")
    public val inputB: InputScalar = input("input_b")
    public val sum: OutputScalar = output("output")
    public val carryOut: OutputScalar = output("carry_out")

    override val kernel: Kernel = kernel {
        declare(inputA)
        declare(inputB)
        declare(sum)
        declare(carryOut)

        onUpdate { context ->
            val a = context[inputA].booleanState()
            val b = context[inputB].booleanState()

            context[sum] = a xor b
            context[carryOut] = a and b
        }
    }
}

public object FullAdder : KernelProvider {
    public val inputA: InputScalar = input("input_a")
    public val inputB: InputScalar = input("input_b")
    public val carryIn: InputScalar = input("carry_in")
    public val sum: OutputScalar = output("output")
    public val carryOut: OutputScalar = output("carry_out")

    override val kernel: Kernel = kernel {
        declare(inputA)
        declare(inputB)
        declare(carryIn)
        declare(sum)
        declare(carryOut)

        onUpdate { context ->
            val a = context[inputA].booleanState()
            val b = context[inputB].booleanState()
            val c = context[carryIn].booleanState()

            context[sum] = a xor b xor c
            context[carryOut] = (a and b) or (a and c) or (b and c)
        }
    }
}
