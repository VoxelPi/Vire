package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

internal interface SettingStateProviderWrapper : SettingStateProvider {

    val settingStateProvider: SettingStateProvider

    override val variableProvider: VariableProvider
        get() = settingStateProvider.variableProvider

    override fun <T> get(setting: Setting<T>): T {
        return settingStateProvider[setting]
    }

    override fun <T> hasValue(setting: Setting<T>): Boolean {
        return settingStateProvider.hasValue(setting)
    }
}

internal interface MutableSettingStateProviderWrapper : SettingStateProviderWrapper, MutableSettingStateProvider {

    override val settingStateProvider: MutableSettingStateProvider

    override fun <T> set(setting: Setting<T>, value: T) {
        settingStateProvider[setting] = value
    }
}
