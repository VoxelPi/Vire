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
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.parameter
import net.voxelpi.vire.engine.kernel.variable.range

/**
 * A multiplexer, see https://en.wikipedia.org/wiki/Multiplexer for more information.
 */
public object Multiplexer : KernelProvider {

    public val addressBits: Parameter<Int> = parameter("address_bits", initialization = { 3 }) {
        range(0..30)
    }

    public val address: InputScalar = input("address")
    public val inputs: InputVector = input("inputs", 0)
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel {
        declare(addressBits)
        declare(address)
        declare(inputs)
        declare(output)

        onConfiguration { context ->
            context.resize(inputs, 1 shl context[addressBits])
        }

        onUpdate { context ->
            // Set output to selected input.
            val selectedIndex = context[address].booleanState(context[addressBits]).toInt()
            context[output] = context[inputs, selectedIndex]
        }
    }
}

/**
 * A demultiplexer, see https://en.wikipedia.org/wiki/Multiplexer for more information.
 */
public object Demultiplexer : KernelProvider {

    public val addressBits: Parameter<Int> = parameter("address_bits", initialization = { 3 }) {
        range(0..30)
    }

    public val address: InputScalar = input("address")
    public val input: InputScalar = input("input")
    public val outputs: OutputVector = output("outputs", 0)

    override val kernel: Kernel = kernel {
        declare(addressBits)
        declare(address)
        declare(input)
        declare(outputs)

        onConfiguration { context ->
            context.resize(outputs, 1 shl context[addressBits])
        }

        onUpdate { context ->
            // Clear all outputs.
            for (i in 0..<context.size(outputs)) {
                context[outputs, i] = LogicState.EMPTY
            }

            // Set selected output to input.
            val selectedIndex = context[address].booleanState(context[addressBits]).toInt()
            context[outputs, selectedIndex] = context[input]
        }
    }
}
