package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider

internal interface KernelInstanceWrapper : KernelVariantWrapper, SettingStateProvider {

    val kernelInstance: KernelInstance

    override val kernelVariant: KernelVariant
        get() = kernelInstance.kernelVariant

    override fun <T> get(setting: Setting<T>): T = kernelInstance[setting]
}
