package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialSettingStateProvider

internal interface SettingStatePatchWrapper : PartialSettingStateProvider {

    val settingStatePatch: SettingStatePatch

    override val variableProvider: VariableProvider
        get() = settingStatePatch.variableProvider

    override fun <T> get(setting: Setting<T>): T {
        return settingStatePatch[setting]
    }

    override fun hasValue(setting: Setting<*>): Boolean {
        return settingStatePatch.hasValue(setting)
    }

    override fun allSettingsSet(): Boolean {
        return settingStatePatch.allSettingsSet()
    }
}

internal interface MutableSettingStatePatchWrapper : SettingStatePatchWrapper, MutablePartialSettingStateProvider {

    override val settingStatePatch: MutableSettingStatePatch

    override fun <T> set(setting: Setting<T>, value: T) {
        settingStatePatch[setting] = value
    }
}
