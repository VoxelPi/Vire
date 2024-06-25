package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

/**
 * A type that provides ways to access the state of a parameter variable.
 */
public interface ParameterStateProvider {

    /**
     * The variable provider for which the parameter states should be provided.
     */
    public val variableProvider: VariableProvider

    /**
     * Returns the current value of the given [parameter].
     *
     * @param parameter the parameter of which the value should be returned.
     */
    public operator fun <T> get(parameter: Parameter<T>): T

    /**
     * Check if the given [parameter] has a set value.
     *
     * @param parameter the parameter which should be checked.
     */
    public fun <T> hasValue(parameter: Parameter<T>): Boolean
}

/**
 * A type that provides ways to access and modify the state of a parameter variable.
 */
public interface MutableParameterStateProvider : ParameterStateProvider {

    /**
     * Sets the value of the given [parameter] to the given [value].
     *
     * @param parameter the parameter of which the value should be modified.
     * @param value the new value of the parameter.
     */
    public operator fun <T> set(parameter: Parameter<T>, value: T)
}
