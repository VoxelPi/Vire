package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateStorageWrapper

internal data class KernelInstanceConfig(
    val kernelVariant: KernelVariantImpl,
    override val settingStateStorage: SettingStateStorage,
) : SettingStateStorageWrapper {

    constructor(kernelVariant: KernelVariantImpl, settingStateProvider: SettingStateProvider) :
        this(kernelVariant, SettingStateStorage(kernelVariant, settingStateProvider))

    constructor(kernelVariant: KernelVariantImpl, settingStateMap: SettingStateMap) :
        this(kernelVariant, SettingStateStorage(kernelVariant, settingStateMap))
}
