package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.MutableSettingStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateStorageWrapper

internal interface SettingStatePatch : PartialSettingStateProvider {

    override val variableProvider: VariableProvider

    val data: SettingStateMap

    fun copy(): SettingStatePatch

    fun mutableCopy(): MutableSettingStatePatch

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(setting: Setting<T>): T {
        // Check that a setting with the given name exists.
        require(variableProvider.hasSetting(setting)) { "Unknown setting ${setting.name}" }

        // Check that the setting has been initialized.
        require(setting.name in data) { "Usage of uninitialized setting ${setting.name}" }

        // Return the value of the setting.
        return data[setting.name] as T
    }

    override fun hasValue(setting: Setting<*>): Boolean {
        return setting.name in data
    }

    fun isComplete(): Boolean {
        return variableProvider.settings().all { hasValue(it) }
    }
}

internal class MutableSettingStatePatch(
    override val variableProvider: VariableProvider,
    override val data: MutableSettingStateMap,
) : SettingStatePatch, MutablePartialSettingStateProvider {

    override fun copy(): SettingStatePatch = mutableCopy()

    override fun mutableCopy(): MutableSettingStatePatch {
        return MutableSettingStatePatch(variableProvider, data.toMutableMap())
    }

    override fun <T> set(setting: Setting<T>, value: T) {
        // Check that a setting with the given name exists.
        require(variableProvider.hasSetting(setting)) { "Unknown setting ${setting.name}" }

        // Check that the value is valid for the specified setting.
        require(setting.isValidValue(value)) { "Value $setting does not meet the requirements for the setting ${setting.name}" }

        // Update the value of the setting.
        data[setting.name] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun update(data: SettingStateMap) {
        for ((settingName, value) in data) {
            // Check that only existing settings are specified.
            val setting = variableProvider.setting(settingName) as Setting<Any?>?
                ?: throw IllegalArgumentException("Unknown setting '$settingName'")

            // Update the value of the setting.
            this[setting] = value
        }
    }
}

internal fun settingStatePatch(variableProvider: VariableProvider, data: SettingStateMap): SettingStatePatch {
    return mutableSettingStatePatch(variableProvider, data)
}

internal fun settingStatePatch(variableProvider: VariableProvider, dataProvider: SettingStateProvider): SettingStatePatch {
    return mutableSettingStatePatch(variableProvider, dataProvider)
}

internal fun mutableSettingStatePatch(variableProvider: VariableProvider, data: SettingStateMap): MutableSettingStatePatch {
    val processedData: MutableSettingStateMap = mutableMapOf()
    for (setting in variableProvider.settings()) {
        // Check if the setting has an assigned value.
        if (setting.name !in data) {
            continue
        }

        // Get the value from the map.
        val value = data[setting.name]

        // Check that the assigned value is valid for the given setting. (Allow null for uninitialized settings).
        if (value != null) {
            require(setting.isValidTypeAndValue(value)) { "Invalid value for the setting ${setting.name}" }
        }

        // Put value into map.
        processedData[setting.name] = value
    }
    return MutableSettingStatePatch(variableProvider, processedData)
}

internal fun mutableSettingStatePatch(
    variableProvider: VariableProvider,
    dataProvider: SettingStateProvider,
): MutableSettingStatePatch {
    val processedData: MutableSettingStateMap = mutableMapOf()
    for (setting in variableProvider.settings()) {
        // Check if the setting has an assigned value.
        if (!dataProvider.variableProvider.hasVariable(setting)) {
            continue
        }
        if (dataProvider is SettingStatePatch && !dataProvider.hasValue(setting)) {
            continue
        }
        if (dataProvider is SettingStateStorageWrapper && !dataProvider.hasValue(setting)) {
            continue
        }

        // Get the value from the provider.
        val value = dataProvider[setting]

        // Check that the assigned value is valid for the given setting (Allow null for uninitialized settings).
        if (value != null) {
            require(setting.isValidTypeAndValue(value)) { "Invalid value for the setting ${setting.name}" }
        }

        // Put value into map.
        processedData[setting.name] = value
    }
    return MutableSettingStatePatch(variableProvider, processedData)
}
