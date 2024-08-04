package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProviderWrapper

public data class KernelInstanceConfig(
    public val kernelVariant: KernelVariant,
    override val settingStateProvider: SettingStateProvider,
) : SettingStateProviderWrapper
