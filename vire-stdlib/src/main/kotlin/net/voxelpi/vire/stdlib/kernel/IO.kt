package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.LogicValue
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.min
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.parameter
import net.voxelpi.vire.stdlib.VIRE_STDLIB_ID

public object Output : KernelProvider {
    public val value: Parameter<LogicValue> = parameter("value", initialization = { LogicValue.NONE })
    public val channels: Parameter<Int> = parameter("channels", initialization = { 1 }) {
        min(1)
    }
    public val output: OutputScalar = output("output") { LogicState.value(this[value], this[channels]) }

    override val kernel: Kernel = kernel(Identifier(VIRE_STDLIB_ID, "input")) {
        declare(value)
        declare(channels)
        declare(output)
    }
}

public object Input : KernelProvider {
    public val input: InputScalar = input("input")

    override val kernel: Kernel = kernel(Identifier(VIRE_STDLIB_ID, "output")) {
        declare(input)
    }
}
