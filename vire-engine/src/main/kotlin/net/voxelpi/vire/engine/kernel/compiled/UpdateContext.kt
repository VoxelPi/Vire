package net.voxelpi.vire.engine.kernel.compiled

import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.MutableKernelState
import net.voxelpi.vire.engine.kernel.MutableKernelStateWrapper
import net.voxelpi.vire.engine.kernel.variable.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariableSizeProvider

public interface UpdateContext :
    VectorVariableSizeProvider,
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
