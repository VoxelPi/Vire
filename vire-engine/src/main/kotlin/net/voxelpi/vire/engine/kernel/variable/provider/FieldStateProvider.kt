package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

/**
 * A type that provides ways to access the state of a field variable.
 */
public interface FieldStateProvider {

    /**
     * The variable provider for which the field states should be provided.
     */
    public val variableProvider: VariableProvider

    /**
     * Returns the current value of the given [field].
     *
     * @param field the variable of which the value should be returned.
     */
    public operator fun <T> get(field: Field<T>): T

    /**
     * Check if the given [field] has a set value.
     *
     * @param field the setting which should be checked.
     */
    public fun <T> hasValue(field: Field<T>): Boolean
}

/**
 * A type that provides ways to access and modify the state of a field variable.
 */
public interface MutableFieldStateProvider : FieldStateProvider {

    /**
     * Sets the value of the given [field] to the given [value].
     *
     * @param field the field of which the value should be modified.
     * @param value the new value of the field.
     */
    public operator fun <T> set(field: Field<T>, value: T)
}
