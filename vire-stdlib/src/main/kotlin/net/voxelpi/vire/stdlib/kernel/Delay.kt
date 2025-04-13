package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.atLeast
import net.voxelpi.vire.engine.kernel.variable.createField
import net.voxelpi.vire.engine.kernel.variable.createInput
import net.voxelpi.vire.engine.kernel.variable.createOutput
import net.voxelpi.vire.engine.kernel.variable.createSetting

public object Delay : KernelProvider {

    /**
     * The input of the delay.
     */
    public val input: InputScalar = createInput("input") {
        description = "The input of the delay."
    }

    /**
     * The output of the delay.
     */
    public val output: OutputScalar = createOutput("output") {
        description = "The output of the delay."
    }

    /**
     * The number of ticks the input is delayed until it is written to the output
     */
    public val delay: Setting<Int> = createSetting("delay") {
        initialization = { 1 }
        constraint = atLeast(1)
        description = "The number of ticks the input is delayed until it is written to the output."
    }

    /**
     * The initial state of the delays output for the first (delay - 1) ticks.
     */
    public val initialization: Setting<LogicState> = createSetting("initialization") {
        initialization = { LogicState.EMPTY }
        description = "The initial state of the delays output for the first (delay - 1) ticks."
    }

    /**
     * Buffer used to store the delayed states.
     */
    public val buffer: Field<Array<LogicState>> = createField("buffer") {
        description = "Buffer used to store the delayed states."
    }

    override val kernel: Kernel = kernel {
        declare(input)
        declare(output)
        declare(delay)
        declare(initialization)
        declare(buffer)

        onInitialization { context ->
            context[buffer] = Array(context[delay] - 1) { context[initialization] }
        }

        onUpdate { context ->
            val buffer = context[buffer]

            // Update buffer.
            context[output] = buffer[0]
            for (i in 0..<(buffer.size - 1)) {
                buffer[i] = buffer[i + 1]
            }
            buffer[buffer.size - 1] = context[input]
        }
    }
}
