package net.voxelpi.vire.engine.circuit.component

import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.variable.Setting

public interface ComponentConfiguration {

    public operator fun <T> get(setting: Setting<T>): Entry<T>

    public operator fun <T> set(setting: Setting<T>, value: Entry<T>)

    public sealed interface Entry<T> {

        public data class Value<T>(val value: T) : Entry<T>

        public data class CircuitSetting<T>(val setting: Setting<T>) : Entry<T>
    }
}

internal class ComponentConfigurationImpl(
    private val kernelVariant: KernelVariant,
) : ComponentConfiguration {

    val settingEntries: MutableMap<String, ComponentConfiguration.Entry<*>> = mutableMapOf()

    init {
        val initialSettingStateProvider = kernelVariant.generateDefaultSettingStates()
        for (setting in kernelVariant.settings()) {
            settingEntries[setting.name] = ComponentConfiguration.Entry.Value(initialSettingStateProvider[setting])
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(setting: Setting<T>): ComponentConfiguration.Entry<T> {
        // Check that the setting exists.
        require(kernelVariant.hasSetting(setting)) { "Unknown setting ${setting.name}" }

        // Return the value of the setting.
        return settingEntries[setting.name]!! as ComponentConfiguration.Entry<T>
    }

    override fun <T> set(setting: Setting<T>, value: ComponentConfiguration.Entry<T>) {
        // Check that a setting with the given name exists.
        require(kernelVariant.hasSetting(setting)) { "Unknown setting ${setting.name}" }

        // Check that the value is valid for the specified setting.
        if (value is ComponentConfiguration.Entry.Value) {
            require(setting.isValidValue(value.value)) { "Value $setting does not meet the requirements for the setting ${setting.name}" }
        }

        // Update the value of the setting.
        settingEntries[setting.name] = value
    }
}
