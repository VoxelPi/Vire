package net.voxelpi.vire.engine.kernel.script

import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.MutableKernelState
import net.voxelpi.vire.engine.kernel.MutableKernelStateWrapper
import net.voxelpi.vire.engine.kernel.variable.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.VectorSizeProvider

public interface UpdateContext :
    VectorSizeProvider,
    ParameterStateProvider,
    SettingStateProvider,
    FieldStateProvider,
    InputStateProvider,
    OutputStateProvider {

    public val kernel: Kernel
}

internal class UpdateContextImpl(
    override val kernelState: MutableKernelState,
) : UpdateContext, MutableKernelStateWrapper {

    override val kernel: Kernel
        get() = super.kernel
}
