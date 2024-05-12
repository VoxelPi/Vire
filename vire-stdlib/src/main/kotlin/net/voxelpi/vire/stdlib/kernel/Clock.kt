package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.field
import net.voxelpi.vire.engine.kernel.variable.min
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.parameter
import net.voxelpi.vire.stdlib.VIRE_STDLIB_ID

public object Clock : KernelProvider {
    public val ticksHigh: Parameter<Long> = parameter("ticks_high", initialization = { 1L }) {
        min(1L)
    }
    public val ticksLow: Parameter<Long> = parameter("ticks_low", initialization = { 1L }) {
        min(1L)
    }
    public val ticks: Field<Long> = field("ticks", initialization = { 0L })
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel(Identifier(VIRE_STDLIB_ID, "clock")) {
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
