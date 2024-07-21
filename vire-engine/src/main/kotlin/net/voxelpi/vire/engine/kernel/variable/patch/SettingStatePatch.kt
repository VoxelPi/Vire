package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.UninitializedVariableException
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.MutableSettingStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateStorage

internal open class SettingStatePatch(
    final override val variableProvider: VariableProvider,
    initialData: SettingStateMap,
) : PartialSettingStateProvider {

    init {
        for ((settingName, settingState) in initialData) {
            val setting = variableProvider.setting(settingName)
                ?: throw IllegalStateException("Data specified for unknown setting \"$settingName\".")

            require(setting.isValidTypeAndValue(settingState)) { "Invalid value specified for setting \"$settingName\"." }
        }
    }

    protected open val data: SettingStateMap = initialData.toMap()

    constructor(variableProvider: VariableProvider, initialData: PartialSettingStateProvider) : this(
        variableProvider,
        variableProvider.settings().filter { initialData.hasValue(it) }.associate { it.name to initialData[it] }
    )

    fun copy(): SettingStatePatch {
        return SettingStatePatch(variableProvider, data)
    }

    fun mutableCopy(): MutableSettingStatePatch {
        return MutableSettingStatePatch(variableProvider, data)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(setting: Setting<T>): T {
        // Check that a setting with the given name exists.
        require(variableProvider.hasSetting(setting)) { "Unknown setting ${setting.name}" }

        // Check that the setting has been initialized.
        if (setting.name !in data) {
            throw UninitializedVariableException(setting)
        }

        // Return the value of the setting.
        return data[setting.name] as T
    }

    override fun hasValue(setting: Setting<*>): Boolean {
        return setting.name in data
    }

    override fun allSettingsSet(): Boolean {
        return variableProvider.settings().all { hasValue(it) }
    }

    /**
     * Creates a setting state storage using the set data.
     * All settings must have a set value otherwise this operation fails.
     */
    fun createStorage(): SettingStateStorage {
        return SettingStateStorage(variableProvider, data)
    }
}

internal class MutableSettingStatePatch(
    variableProvider: VariableProvider,
    initialData: SettingStateMap,
) : SettingStatePatch(variableProvider, initialData), MutablePartialSettingStateProvider {

    override val data: MutableSettingStateMap = initialData.toMutableMap()

    constructor(variableProvider: VariableProvider, initialData: PartialSettingStateProvider) : this(
        variableProvider,
        variableProvider.settings().filter { initialData.hasValue(it) }.associate { it.name to initialData[it] }
    )

    override fun <T> set(setting: Setting<T>, value: T) {
        // Check that a setting with the given name exists.
        require(variableProvider.hasSetting(setting)) { "Unknown setting ${setting.name}" }

        // Check that the value is valid for the specified setting.
        require(setting.isValidTypeAndValue(value)) { "Value $value does not meet the requirements for the setting ${setting.name}" }

        // Update the value of the setting.
        data[setting.name] = value
    }
}
