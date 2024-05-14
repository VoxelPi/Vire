package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.min
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.parameter
import net.voxelpi.vire.stdlib.VIRE_STDLIB_ID

public object Packager : KernelProvider {
    public val blockCount: Parameter<Int> = parameter("block_count", initialization = { 2 }) {
        min(1)
    }
    public val blockSize: Parameter<Int> = parameter("block_size", initialization = { 1 }) {
        min(1)
    }
    public val input: InputVector = input("input", blockCount)
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel(Identifier(VIRE_STDLIB_ID, "packager")) {
        declare(blockCount)
        declare(blockSize)
        declare(input)
        declare(output)

        onUpdate { context ->
            val count = context[blockCount]
            val size = context[blockSize]
            context[output] = LogicState(count * size) { index ->
                val inputIndex = index / size
                val inputChannel = index % size
                context[input, inputIndex][inputChannel]
            }
        }
    }
}

public object Unpackager : KernelProvider {
    public val blockCount: Parameter<Int> = parameter("block_count", initialization = { 2 }) {
        min(1)
    }
    public val blockSize: Parameter<Int> = parameter("block_size", initialization = { 1 }) {
        min(1)
    }
    public val input: InputScalar = input("input")
    public val output: OutputVector = output("output", blockCount)

    override val kernel: Kernel = kernel(Identifier(VIRE_STDLIB_ID, "unpackager")) {
        declare(blockCount)
        declare(blockSize)
        declare(input)
        declare(output)

        onUpdate { context ->
            val count = context[blockCount]
            val size = context[blockSize]
            for (index in 0..<(count * size)) {
                val outputIndex = index / size
                val outputChannel = index % size
                context[output, outputIndex] = LogicState.value(context[input].channelOrNone(outputChannel))
            }
        }
    }
}
