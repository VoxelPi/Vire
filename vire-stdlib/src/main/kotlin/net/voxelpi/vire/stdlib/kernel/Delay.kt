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

    public val input: InputScalar = createInput("input")

    public val output: OutputScalar = createOutput("output")

    /**
     * The delay in ticks until the input is written to the output.
     */
    public val delay: Setting<Int> = createSetting("delay") {
        initialization = { 1 }
        constraint = atLeast(1)
    }

    /**
     * The initial state of delays output for the first (delay - 1) ticks.
     */
    public val initialization: Setting<LogicState> = createSetting("initialization") {
        initialization = { LogicState.EMPTY }
    }

    /**
     * The internal buffer used by the delay to store the state.
     */
    public val buffer: Field<Array<LogicState>> = createField("buffer")

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
