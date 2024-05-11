package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.SettingProvider
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider

public interface SimulationConfiguration : SettingStateProvider {

    public val kernelVariant: KernelVariant
}

internal class SimulationConfigurationImpl(
    override val kernelVariant: KernelVariant,
) : SimulationConfiguration {

    override val settingProvider: SettingProvider
        get() = TODO("Not yet implemented")

    override fun <T> get(setting: Setting<T>): T {
        TODO("Not yet implemented")
    }
}
