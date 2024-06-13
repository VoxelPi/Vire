package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.setting

public object Output : KernelProvider {
    public val value: Setting<LogicState> = setting("value", initialization = { LogicState.EMPTY })
    public val output: OutputScalar = output("output") { this[value] }

    override val kernel: Kernel = kernel {
        declare(value)
        declare(output)
    }
}

public object Input : KernelProvider {
    public val input: InputScalar = input("input")

    override val kernel: Kernel = kernel {
        declare(input)
    }
}
