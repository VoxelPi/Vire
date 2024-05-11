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
        require(variableProvider.hasSetting(setting.name)) { "Unknown setting ${setting.name}" }

        // Return the value of the setting.
        return data[setting.name] as T
    }
}

internal class MutableSettingStateStorage(
    override val variableProvider: VariableProvider,
    override val data: MutableSettingStateMap,
) : SettingStateStorage, MutableSettingStateProvider {

    override fun copy(): MutableSettingStateStorage = mutableCopy()

    override fun mutableCopy(): MutableSettingStateStorage {
        return MutableSettingStateStorage(variableProvider, data.toMutableMap())
    }

    override fun <T> set(setting: Setting<T>, value: T) {
        // Check that a setting with the given name exists.
        require(variableProvider.hasSetting(setting.name)) { "Unknown setting ${setting.name}" }

        // Check that the value is valid for the specified setting.
        require(setting.isValidValue(value)) { "Value $setting does not meet the requirements for the setting ${setting.name}" }

        // Update the value of the setting.
        data[setting.name] = value
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
        // Check that the setting has an assigned value.
        require(setting.name in data) { "No value provided for the setting ${setting.name}" }

        // Get the value from the map.
        val value = data[setting.name]

        // Check that the assigned value is valid for the given setting.
        require(setting.isValidTypeAndValue(value)) { "Invalid value for the setting ${setting.name}" }

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
        // Check that the setting has an assigned value.
        require(dataProvider.variableProvider.hasVariable(setting)) { "No value provided for the setting ${setting.name}" }

        // Get the value from the provider.
        val value = dataProvider[setting]

        // Check that the assigned value is valid for the given setting.
        require(setting.isValidTypeAndValue(value)) { "Invalid value for the setting ${setting.name}" }

        // Put value into map.
        processedData[setting.name] = value
    }
    return MutableSettingStateStorage(variableProvider, processedData)
}
