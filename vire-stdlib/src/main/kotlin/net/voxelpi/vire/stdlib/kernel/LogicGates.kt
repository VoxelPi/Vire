package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.BooleanState
import net.voxelpi.vire.engine.booleanStates
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.min
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.parameter

public object BufferGate : KernelProvider {
    public val input: InputScalar = input("input")
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel {
        declare(input)
        declare(output)

        onUpdate { context ->
            context[output] = context[input].booleanState()
        }
    }
}

public object NotGate : KernelProvider {
    public val input: InputScalar = input("input")
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel {
        declare(BufferGate.input)
        declare(BufferGate.output)

        onUpdate { context ->
            context[output] = !context[input].booleanState()
        }
    }
}

public object AndGate : KernelProvider {
    public val inputSize: Parameter<Int> = parameter("input_count", initialization = { 2 }) {
        min(2)
    }
    public val inputs: InputVector = input("inputs", inputSize)
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel {
        declare(inputSize)
        declare(inputs)
        declare(output)

        onUpdate { context ->
            context[output] = BooleanState.and(context[OrGate.inputs].booleanStates())
        }
    }
}

public object OrGate : KernelProvider {
    public val inputSize: Parameter<Int> = parameter("input_count", initialization = { 2 }) {
        min(2)
    }
    public val inputs: InputVector = input("inputs", inputSize)
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel {
        declare(inputSize)
        declare(inputs)
        declare(output)

        onUpdate { context ->
            context[output] = BooleanState.or(context[inputs].booleanStates())
        }
    }
}

public object XorGate : KernelProvider {
    public val inputSize: Parameter<Int> = parameter("input_count", initialization = { 2 }) {
        min(2)
    }
    public val inputs: InputVector = input("inputs", inputSize)
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel {
        declare(inputSize)
        declare(inputs)
        declare(output)

        onUpdate { context ->
            context[output] = BooleanState.xor(context[OrGate.inputs].booleanStates())
        }
    }
}

public object NandGate : KernelProvider {
    public val inputSize: Parameter<Int> = parameter("input_count", initialization = { 2 }) {
        min(2)
    }
    public val inputs: InputVector = input("inputs", inputSize)
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel {
        declare(inputSize)
        declare(inputs)
        declare(output)

        onUpdate { context ->
            context[output] = BooleanState.nand(context[OrGate.inputs].booleanStates())
        }
    }
}

public object NorGate : KernelProvider {
    public val inputSize: Parameter<Int> = parameter("input_count", initialization = { 2 }) {
        min(2)
    }
    public val inputs: InputVector = input("inputs", inputSize)
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel {
        declare(inputSize)
        declare(inputs)
        declare(output)

        onUpdate { context ->
            context[output] = BooleanState.nor(context[OrGate.inputs].booleanStates())
        }
    }
}

public object XnorGate : KernelProvider {
    public val inputSize: Parameter<Int> = parameter("input_count", initialization = { 2 }) {
        min(2)
    }
    public val inputs: InputVector = input("inputs", inputSize)
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel {
        declare(inputSize)
        declare(inputs)
        declare(output)

        onUpdate { context ->
            context[output] = BooleanState.xnor(context[OrGate.inputs].booleanStates())
        }
    }
}
