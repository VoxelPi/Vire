package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProviderWrapper
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

    public fun copy(): KernelState

    public fun mutableCopy(): MutableKernelState
}

public interface MutableKernelState : KernelState, MutableFieldStateProvider, MutableInputStateProvider, MutableOutputStateProvider

internal class MutableKernelStateImpl(
    override val kernel: Kernel,
    override val variableProvider: VariableProvider,
    override val vectorSizeProvider: VectorSizeProvider,
    override val parameterStateProvider: ParameterStateProvider,
    override val settingStateProvider: SettingStateProvider,
    override val fieldStateStorage: MutableFieldStateStorage,
    override val inputStateStorage: MutableInputStateStorage,
    override val outputStateStorage: MutableOutputStateStorage,
) : MutableKernelState,
    VectorSizeProviderWrapper,
    ParameterStateProviderWrapper,
    SettingStateProviderWrapper,
    MutableFieldStateStorageWrapper,
    MutableInputStateStorageWrapper,
    MutableOutputStateStorageWrapper {

    override fun copy(): KernelState {
        return MutableKernelStateImpl(
            kernel,
            variableProvider,
            vectorSizeProvider,
            parameterStateProvider,
            settingStateProvider,
            fieldStateStorage.mutableCopy(),
            inputStateStorage.mutableCopy(),
            outputStateStorage.mutableCopy()
        )
    }

    override fun mutableCopy(): MutableKernelStateImpl {
        return MutableKernelStateImpl(
            kernel,
            variableProvider,
            vectorSizeProvider,
            parameterStateProvider,
            settingStateProvider,
            fieldStateStorage.mutableCopy(),
            inputStateStorage.mutableCopy(),
            outputStateStorage.mutableCopy()
        )
    }
}
