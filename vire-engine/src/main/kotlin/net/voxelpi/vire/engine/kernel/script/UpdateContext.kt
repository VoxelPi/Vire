package net.voxelpi.vire.engine.kernel.script

import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.MutableKernelState
import net.voxelpi.vire.engine.kernel.MutableKernelStateWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider

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
