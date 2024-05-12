package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.KernelInstance
import net.voxelpi.vire.engine.kernel.KernelState
import net.voxelpi.vire.engine.kernel.KernelStateWrapper
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.storage.MutableInputStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableInputStateStorageWrapper

public interface SimulationInputConfiguration :
    ParameterStateProvider,
    SettingStateProvider,
    MutableInputStateProvider,
    VectorSizeProvider {

    /**
     * The kernel which of which the instance was created.
     */
    public val kernelVariant: KernelVariant
}

internal class SimulationInputConfigurationImpl(
    override val kernelState: KernelState,
    override val inputStateStorage: MutableInputStateStorage,
) : SimulationInputConfiguration, MutableInputStateStorageWrapper, KernelStateWrapper {

    override val kernelInstance: KernelInstance
        get() = kernelState.kernelInstance

    override val kernelVariant: KernelVariant
        get() = kernelInstance.kernelVariant

    override val variableProvider: VariableProvider
        get() = kernelVariant

    override fun get(input: InputScalar): LogicState = super<MutableInputStateStorageWrapper>.get(input)

    override fun get(inputVector: InputVector): Array<LogicState> = super<MutableInputStateStorageWrapper>.get(inputVector)

    override fun get(inputVector: InputVector, index: Int): LogicState = super<MutableInputStateStorageWrapper>.get(inputVector, index)
}
