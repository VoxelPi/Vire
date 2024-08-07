package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider

public typealias SettingStateMap = Map<String, Any?>

public typealias MutableSettingStateMap = MutableMap<String, Any?>

/**
 * A collection that stores the state of all settings of the given [variableProvider].
 */
public open class SettingStateStorage(
    final override val variableProvider: VariableProvider,
    initialData: SettingStateMap,
) : SettingStateProvider {

    init {
        for ((settingName, settingState) in initialData) {
            val setting = variableProvider.setting(settingName)
                ?: throw IllegalStateException("Data specified for unknown setting \"$settingName\".")

            require(setting.isValidTypeAndValue(settingState)) { "Invalid value specified for setting \"$settingName\"." }
        }

        val missingVariables = variableProvider.settings().map { it.name }.filter { it !in initialData }
        require(missingVariables.isEmpty()) {
            "Missing values for the following settings: ${missingVariables.joinToString(", ") { "\"${it}\"" } }"
        }
    }

    protected open val data: SettingStateMap = initialData.toMap()

    public constructor(variableProvider: VariableProvider, initialData: SettingStateProvider) : this(
        variableProvider,
        variableProvider.settings().filter { initialData.hasValue(it) }.associate { it.name to initialData[it] }
    )

    /**
     * Creates a copy of this storage.
     */
    public fun copy(): SettingStateStorage {
        return SettingStateStorage(variableProvider, data)
    }

    /**
     * Creates a mutable copy of this storage.
     */
    public fun mutableCopy(): MutableSettingStateStorage {
        return MutableSettingStateStorage(variableProvider, data)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(setting: Setting<T>): T {
        // Check that a setting with the given name exists.
        require(variableProvider.hasSetting(setting)) { "Unknown setting ${setting.name}" }

        // Return the value of the setting.
        return data[setting.name] as T
    }
}

/**
 * A mutable collection that stores the state of all settings of the given [variableProvider].
 */
public class MutableSettingStateStorage(
    variableProvider: VariableProvider,
    initialData: SettingStateMap,
) : SettingStateStorage(variableProvider, initialData), MutableSettingStateProvider {

    override val data: MutableSettingStateMap = initialData.toMutableMap()

    public constructor(variableProvider: VariableProvider, initialData: SettingStateProvider) : this(
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
