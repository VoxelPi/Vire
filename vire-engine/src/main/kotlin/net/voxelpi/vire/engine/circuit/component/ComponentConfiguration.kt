package net.voxelpi.vire.engine.circuit.component

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

public interface ComponentConfiguration {

    public fun <T> get(setting: Setting<T>): Entry<T>

    public fun <T> set(setting: Setting<T>, value: Entry<T>)

    public sealed interface Entry<T> {

        public data class Value<T>(val value: T) : Entry<T>

        public data class CircuitSetting<T>(val setting: Setting<T>) : Entry<T>
    }
}

internal class ComponentConfigurationImpl(private val settingProvider: VariableProvider) : ComponentConfiguration {

    private val settingEntries: MutableMap<String, ComponentConfiguration.Entry<*>> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(setting: Setting<T>): ComponentConfiguration.Entry<T> {
        // Check that the setting exists.
        require(settingProvider.hasSetting(setting)) { "Unknown setting ${setting.name}" }

        // Return the value of the setting.
        return settingEntries[setting.name]!! as ComponentConfiguration.Entry<T>
    }

    override fun <T> set(setting: Setting<T>, value: ComponentConfiguration.Entry<T>) {
        // Check that a setting with the given name exists.
        require(settingProvider.hasSetting(setting)) { "Unknown setting ${setting.name}" }

        // Check that the value is valid for the specified setting.
        if (value is ComponentConfiguration.Entry.Value) {
            require(setting.isValidValue(value.value)) { "Value $setting does not meet the requirements for the setting ${setting.name}" }
        }

        // Update the value of the setting.
        settingEntries[setting.name] = value
    }
}
