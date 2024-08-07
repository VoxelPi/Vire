package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.patch.SettingStatePatch
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateMap

/**
 * A type that provides access to the state of some of the registered setting variables.
 */
public interface PartialSettingStateProvider {

    /**
     * The variable provider for which the setting states should be provided.
     */
    public val variableProvider: VariableProvider

    /**
     * Returns the current value of the given [setting] or null if the setting has no value set.
     *
     * @param setting the variable of which the value should be returned.
     */
    public operator fun <T> get(setting: Setting<T>): T

    /**
     * Returns if the given setting has a set value.
     */
    public fun hasValue(setting: Setting<*>): Boolean

    /**
     * Checks if all registered settings have a set value.
     */
    public fun allSettingsSet(): Boolean
}

/**
 * A type that provides mutable access to the state of some of the registered setting variables.
 */
public interface MutablePartialSettingStateProvider : PartialSettingStateProvider {

    /**
     * Sets the value of the given [setting] to the given [value].
     *
     * @param setting the setting of which the value should be modified.
     * @param value the new value of the setting.
     */
    public operator fun <T> set(setting: Setting<T>, value: T)

    /**
     * Copies all values present in the given [provider] to this provider.
     */
    @Suppress("UNCHECKED_CAST")
    public fun applySettingStatePatch(provider: PartialSettingStateProvider) {
        for (setting in provider.variableProvider.settings().filter(provider::hasValue)) {
            this[(setting as Setting<Any?>)] = provider[setting]
        }
    }

    /**
     * Copies all values present in the given [map] to this provider.
     */
    public fun applySettingStatePatch(map: SettingStateMap) {
        applySettingStatePatch(SettingStatePatch(variableProvider, map))
    }
}

/**
 * A type that provides access to the state of all registered setting variables.
 */
public interface SettingStateProvider : PartialSettingStateProvider {

    /**
     * Returns the current value of the given [setting].
     *
     * @param setting the variable of which the value should be returned.
     */
    override fun <T> get(setting: Setting<T>): T

    override fun hasValue(setting: Setting<*>): Boolean = setting in variableProvider.settings()

    override fun allSettingsSet(): Boolean = true
}

/**
 * A type that provides mutable access to the state of all registered setting variables.
 */
public interface MutableSettingStateProvider : SettingStateProvider, MutablePartialSettingStateProvider
