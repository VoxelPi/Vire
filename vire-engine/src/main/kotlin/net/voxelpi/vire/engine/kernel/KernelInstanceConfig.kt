package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.SettingStateMap
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.SettingStateStorage
import net.voxelpi.vire.engine.kernel.variable.SettingStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.settingStateStorage

internal data class KernelInstanceConfig(
    val kernelVariant: KernelVariantImpl,
    override val settingStateStorage: SettingStateStorage,
) : SettingStateStorageWrapper {

    constructor(kernelVariant: KernelVariantImpl, settingStateProvider: SettingStateProvider) :
        this(kernelVariant, settingStateStorage(kernelVariant, settingStateProvider))

    constructor(kernelVariant: KernelVariantImpl, settingStateMap: SettingStateMap) :
        this(kernelVariant, settingStateStorage(kernelVariant, settingStateMap))
}
