package net.voxelpi.vire.stdlib.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.StateMachineProvider
import net.voxelpi.vire.api.circuit.statemachine.input
import net.voxelpi.vire.api.circuit.statemachine.output
import net.voxelpi.vire.api.circuit.statemachine.parameter
import net.voxelpi.vire.stdlib.VIRE_STDLIB_ID

object Packager : StateMachineProvider {
    val blockCount = parameter("block_count", 2, min = 2)
    val blockSize = parameter("block_size", 1, min = 1)
    val input = input("input", blockCount)
    val output = output("output")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "packager")) {
        declare(blockCount)
        declare(blockSize)
        declare(input)
        declare(output)

        update = { context ->
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

object Unpackager : StateMachineProvider {
    val blockCount = parameter("block_count", 2, min = 2)
    val blockSize = parameter("block_size", 1, min = 1)
    val input = input("input")
    val output = output("output", blockCount)

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "unpackager")) {
        declare(blockCount)
        declare(blockSize)
        declare(input)
        declare(output)

        update = { context ->
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
