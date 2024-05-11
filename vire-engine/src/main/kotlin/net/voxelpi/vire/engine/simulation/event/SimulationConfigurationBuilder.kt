package net.voxelpi.vire.engine.simulation.event

import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.variable.MutableSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.MutableSettingStateStorage
import net.voxelpi.vire.engine.kernel.variable.MutableSettingStateStorageWrapper

public interface SimulationConfigurationBuilder : MutableSettingStateProvider {

    public val kernelVariant: KernelVariant

    public val kernel: Kernel
        get() = kernelVariant.kernel
}

internal class SimulationConfigurationBuilderImpl(
    override val kernelVariant: KernelVariant,
) : SimulationConfigurationBuilder, MutableSettingStateStorageWrapper {

    override val settingStateStorage: MutableSettingStateStorage
        get() = TODO("Not yet implemented")

    override val kernel: Kernel
        get() = super.kernel
}
