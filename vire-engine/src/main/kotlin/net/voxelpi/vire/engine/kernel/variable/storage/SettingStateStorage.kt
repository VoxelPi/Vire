package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider

internal typealias SettingStateMap = Map<String, Any?>

internal typealias MutableSettingStateMap = MutableMap<String, Any?>

internal interface SettingStateStorage : SettingStateProvider {

    override val variableProvider: VariableProvider

    val data: SettingStateMap

    fun copy(): SettingStateStorage

    fun mutableCopy(): MutableSettingStateStorage

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(setting: Setting<T>): T {
        // Check that a setting with the given name exists.
        require(variableProvider.hasSetting(setting)) { "Unknown setting ${setting.name}" }

        // Check that the setting has been initialized.
        require(setting.name in data) { "Usage of uninitialized setting ${setting.name}" }

        // Return the value of the setting.
        return data[setting.name] as T
    }

    override fun <T> hasValue(setting: Setting<T>): Boolean {
        return setting.name in data
    }

    fun isComplete(): Boolean {
        return variableProvider.settings().all { hasValue(it) }
    }
}

internal class MutableSettingStateStorage(
    override val variableProvider: VariableProvider,
    override val data: MutableSettingStateMap,
) : SettingStateStorage, MutableSettingStateProvider {

    override fun copy(): SettingStateStorage = mutableCopy()

    override fun mutableCopy(): MutableSettingStateStorage {
        return MutableSettingStateStorage(variableProvider, data.toMutableMap())
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

internal fun settingStateStorage(variableProvider: VariableProvider, data: SettingStateMap): SettingStateStorage {
    return mutableSettingStateStorage(variableProvider, data)
}

internal fun settingStateStorage(variableProvider: VariableProvider, dataProvider: SettingStateProvider): SettingStateStorage {
    return mutableSettingStateStorage(variableProvider, dataProvider)
}

internal fun mutableSettingStateStorage(variableProvider: VariableProvider, data: SettingStateMap): MutableSettingStateStorage {
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
    return MutableSettingStateStorage(variableProvider, processedData)
}

internal fun mutableSettingStateStorage(
    variableProvider: VariableProvider,
    dataProvider: SettingStateProvider,
): MutableSettingStateStorage {
    val processedData: MutableSettingStateMap = mutableMapOf()
    for (setting in variableProvider.settings()) {
        // Check if the setting has an assigned value.
        if (!dataProvider.hasValue(setting)) {
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
    return MutableSettingStateStorage(variableProvider, processedData)
}
