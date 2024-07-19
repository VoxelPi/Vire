package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

/**
 * A type that provides access to the state of some of the registered parameter variables.
 */
public interface PartialParameterStateProvider {

    /**
     * The variable provider for which the parameter states should be provided.
     */
    public val variableProvider: VariableProvider

    /**
     * Returns the current value of the given [parameter] or null if the parameter has no value set.
     *
     * @param parameter the variable of which the value should be returned.
     */
    public operator fun <T> get(parameter: Parameter<T>): T?

    /**
     * Returns if the given parameter has a set value.
     */
    public fun hasValue(parameter: Parameter<*>): Boolean
}

/**
 * A type that provides mutable access to the state of some of the registered parameter variables.
 */
public interface MutablePartialParameterStateProvider : PartialParameterStateProvider {

    /**
     * Sets the value of the given [parameter] to the given [value].
     *
     * @param parameter the parameter of which the value should be modified.
     * @param value the new value of the parameter.
     */
    public operator fun <T> set(parameter: Parameter<T>, value: T)
}

/**
 * A type that provides access to the state of all registered parameter variables.
 */
public interface ParameterStateProvider : PartialParameterStateProvider {

    /**
     * Returns the current value of the given [parameter].
     *
     * @param parameter the parameter of which the value should be returned.
     */
    override fun <T> get(parameter: Parameter<T>): T

    override fun hasValue(parameter: Parameter<*>): Boolean = parameter in variableProvider
}

/**
 * A type that provides mutable access to the state of all registered parameter variables.
 */
public interface MutableParameterStateProvider : ParameterStateProvider, MutablePartialParameterStateProvider
