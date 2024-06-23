package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.atLeast
import net.voxelpi.vire.engine.kernel.variable.createField
import net.voxelpi.vire.engine.kernel.variable.createOutput
import net.voxelpi.vire.engine.kernel.variable.createSetting

public object Clock : KernelProvider {
    public val ticksHigh: Setting<Long> = createSetting("ticks_high") {
        initialization = { 1L }
        constraint = atLeast(1L)
    }
    public val ticksLow: Setting<Long> = createSetting("ticks_low") {
        initialization = { 1L }
        constraint = atLeast(1L)
    }
    public val ticks: Field<Long> = createField("ticks") {
        initialization = { 0L }
    }
    public val output: OutputScalar = createOutput("output")

    override val kernel: Kernel = kernel {
        declare(ticksHigh)
        declare(ticksLow)
        declare(ticks)
        declare(output)

        onUpdate { context ->
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
