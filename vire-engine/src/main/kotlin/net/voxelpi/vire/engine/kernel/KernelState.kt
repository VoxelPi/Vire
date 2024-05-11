package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.MutableFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.MutableFieldStateStorage
import net.voxelpi.vire.engine.kernel.variable.MutableFieldStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.MutableInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.MutableInputStateStorage
import net.voxelpi.vire.engine.kernel.variable.MutableInputStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.MutableOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.MutableOutputStateStorage
import net.voxelpi.vire.engine.kernel.variable.MutableOutputStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.VectorSizeProvider

public interface KernelState :
    VectorSizeProvider,
    ParameterStateProvider,
    SettingStateProvider,
    FieldStateProvider,
    InputStateProvider,
    OutputStateProvider {

    public val kernel: Kernel

    public val kernelVariant: KernelVariant

    public val kernelInstance: KernelInstance

    public fun clone(): KernelState

    public fun mutableClone(): MutableKernelState
}

public interface MutableKernelState : KernelState, MutableFieldStateProvider, MutableInputStateProvider, MutableOutputStateProvider

internal class KernelStateImpl(
    override val kernelInstance: KernelInstanceImpl,
    override val fieldStateStorage: MutableFieldStateStorage,
    override val inputStateStorage: MutableInputStateStorage,
    override val outputStateStorage: MutableOutputStateStorage,
) : MutableKernelState,
    MutableFieldStateStorageWrapper,
    MutableInputStateStorageWrapper,
    MutableOutputStateStorageWrapper,
    KernelInstanceWrapper {

    override val kernel: Kernel
        get() = kernelVariant.kernel

    override val kernelVariant: KernelVariant
        get() = kernelInstance.kernelVariant

    override fun clone(): KernelState {
        TODO("Not yet implemented")
    }

    override fun mutableClone(): MutableKernelState {
        TODO("Not yet implemented")
    }
}
