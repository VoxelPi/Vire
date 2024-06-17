package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.atLeast
import net.voxelpi.vire.engine.kernel.variable.createInput
import net.voxelpi.vire.engine.kernel.variable.createInputVector
import net.voxelpi.vire.engine.kernel.variable.createOutput
import net.voxelpi.vire.engine.kernel.variable.createOutputVector
import net.voxelpi.vire.engine.kernel.variable.createParameter

public object Packager : KernelProvider {
    public val blockCount: Parameter<Int> = createParameter("block_count") {
        initialization = { 2 }
        constraint = atLeast(1)
    }
    public val blockSize: Parameter<Int> = createParameter("block_size") {
        initialization = { 1 }
        constraint = atLeast(1)
    }
    public val input: InputVector = createInputVector("input") {
        size = { this[blockCount] }
    }
    public val output: OutputScalar = createOutput("output")

    override val kernel: Kernel = kernel {
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
    public val blockCount: Parameter<Int> = createParameter("block_count") {
        initialization = { 2 }
        constraint = atLeast(1)
    }
    public val blockSize: Parameter<Int> = createParameter("block_size") {
        initialization = { 1 }
        constraint = atLeast(1)
    }
    public val input: InputScalar = createInput("input")
    public val output: OutputVector = createOutputVector("output") {
        size = { this[blockCount] }
    }

    override val kernel: Kernel = kernel {
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
