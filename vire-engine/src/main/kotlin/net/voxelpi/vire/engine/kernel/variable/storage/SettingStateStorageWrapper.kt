package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider

internal interface SettingStateStorageWrapper : SettingStateProvider {

    val settingStateStorage: SettingStateStorage

    override val variableProvider: VariableProvider
        get() = settingStateStorage.variableProvider

    override fun <T> get(setting: Setting<T>): T {
        return settingStateStorage[setting]
    }

    fun <T> hasValue(setting: Setting<T>): Boolean {
        return settingStateStorage.hasValue(setting)
    }
}

internal interface MutableSettingStateStorageWrapper : SettingStateStorageWrapper, MutableSettingStateProvider {

    override val settingStateStorage: MutableSettingStateStorage

    override fun <T> set(setting: Setting<T>, value: T) {
        settingStateStorage[setting] = value
    }
}
