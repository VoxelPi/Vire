package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.storage.MutableFieldStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableFieldStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.storage.MutableInputStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableInputStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.storage.MutableOutputStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableOutputStateStorageWrapper

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

    override val variableProvider: VariableProvider
        get() = kernelVariant

    override fun clone(): KernelState {
        TODO("Not yet implemented")
    }

    override fun mutableClone(): MutableKernelState {
        TODO("Not yet implemented")
    }
}
