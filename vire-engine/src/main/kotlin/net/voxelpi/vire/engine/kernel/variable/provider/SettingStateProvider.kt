package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

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
    public operator fun <T> get(setting: Setting<T>): T?

    /**
     * Returns if the given setting has a set value.
     */
    public fun hasValue(setting: Setting<*>): Boolean
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

    override fun hasValue(setting: Setting<*>): Boolean = setting in variableProvider
}

/**
 * A type that provides mutable access to the state of all registered setting variables.
 */
public interface MutableSettingStateProvider : SettingStateProvider, MutablePartialSettingStateProvider
