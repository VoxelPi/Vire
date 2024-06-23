package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.createInput
import net.voxelpi.vire.engine.kernel.variable.createOutput
import net.voxelpi.vire.engine.kernel.variable.createSetting

public object Output : KernelProvider {
    public val value: Setting<LogicState> = createSetting("value") {
        initialization = { LogicState.EMPTY }
    }
    public val output: OutputScalar = createOutput("output") {
        initialization = { this[value] }
    }

    override val kernel: Kernel = kernel {
        declare(value)
        declare(output)
    }
}

public object Input : KernelProvider {
    public val input: InputScalar = createInput("input")

    override val kernel: Kernel = kernel {
        declare(input)
    }
}
