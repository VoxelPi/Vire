package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

/**
 * A type that provides ways to access the state of a setting variable.
 */
public interface SettingStateProvider {

    /**
     * The variable provider for which the setting states should be provided.
     */
    public val variableProvider: VariableProvider

    /**
     * Returns the current value of the given [setting].
     *
     * @param setting the variable of which the value should be returned.
     */
    public operator fun <T> get(setting: Setting<T>): T

    /**
     * Check if the given [setting] has a set value.
     *
     * @param setting the setting which should be checked.
     */
    public fun <T> hasValue(setting: Setting<T>): Boolean
}

/**
 * A type that provides ways to access and modify the state of a setting variable.
 */
public interface MutableSettingStateProvider : SettingStateProvider {

    /**
     * Sets the value of the given [setting] to the given [value].
     *
     * @param setting the setting of which the value should be modified.
     * @param value the new value of the setting.
     */
    public operator fun <T> set(setting: Setting<T>, value: T)
}
