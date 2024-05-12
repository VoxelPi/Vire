package net.voxelpi.vire.engine.kernel.script

import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.MutableKernelState
import net.voxelpi.vire.engine.kernel.MutableKernelStateWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider

public interface UpdateContext :
    VectorSizeProvider,
    ParameterStateProvider,
    SettingStateProvider,
    MutableFieldStateProvider,
    InputStateProvider,
    MutableOutputStateProvider {

    public val kernel: Kernel
}

internal class UpdateContextImpl(
    override val kernelState: MutableKernelState,
) : UpdateContext, MutableKernelStateWrapper {

    override val kernel: Kernel
        get() = super.kernel
}
