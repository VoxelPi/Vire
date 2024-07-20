package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

internal interface PartialSettingStateProviderWrapper : PartialSettingStateProvider {

    val settingStateProvider: PartialSettingStateProvider

    override val variableProvider: VariableProvider
        get() = settingStateProvider.variableProvider

    override fun <T> get(setting: Setting<T>): T {
        return settingStateProvider[setting]
    }

    override fun hasValue(setting: Setting<*>): Boolean {
        return settingStateProvider.hasValue(setting)
    }

    override fun allSettingsSet(): Boolean {
        return settingStateProvider.allSettingsSet()
    }
}

internal interface MutablePartialSettingStateProviderWrapper : PartialSettingStateProviderWrapper, MutablePartialSettingStateProvider {

    override val settingStateProvider: MutablePartialSettingStateProvider

    override fun <T> set(setting: Setting<T>, value: T) {
        settingStateProvider[setting] = value
    }
}

internal interface SettingStateProviderWrapper : SettingStateProvider {

    val settingStateProvider: SettingStateProvider

    override val variableProvider: VariableProvider
        get() = settingStateProvider.variableProvider

    override fun <T> get(setting: Setting<T>): T {
        return settingStateProvider[setting]
    }
}

internal interface MutableSettingStateProviderWrapper : SettingStateProviderWrapper, MutableSettingStateProvider {

    override val settingStateProvider: MutableSettingStateProvider

    override fun <T> set(setting: Setting<T>, value: T) {
        settingStateProvider[setting] = value
    }
}
