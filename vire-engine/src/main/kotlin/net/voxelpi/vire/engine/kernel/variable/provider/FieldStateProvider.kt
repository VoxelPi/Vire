package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

/**
 * A type that provides access to the state of some of the registered field variables.
 */
public interface PartialFieldStateProvider {

    /**
     * The variable provider for which the field states should be provided.
     */
    public val variableProvider: VariableProvider

    /**
     * Returns the current value of the given [field] or null if the field has no value set.
     *
     * @param field the variable of which the value should be returned.
     */
    public operator fun <T> get(field: Field<T>): T?

    /**
     * Returns if the given field has a set value.
     */
    public fun hasValue(field: Field<*>): Boolean
}

/**
 * A type that provides mutable access to the state of some of the registered field variables.
 */
public interface MutablePartialFieldStateProvider : PartialFieldStateProvider {

    /**
     * Sets the value of the given [field] to the given [value].
     *
     * @param field the field of which the value should be modified.
     * @param value the new value of the field.
     */
    public operator fun <T> set(field: Field<T>, value: T)
}

/**
 * A type that provides access to the state of all registered field variables.
 */
public interface FieldStateProvider : PartialFieldStateProvider {

    /**
     * Returns the current value of the given [field].
     *
     * @param field the variable of which the value should be returned.
     */
    override fun <T> get(field: Field<T>): T

    override fun hasValue(field: Field<*>): Boolean = field in variableProvider
}

/**
 * A type that provides mutable access to the state of all registered field variables.
 */
public interface MutableFieldStateProvider : FieldStateProvider, MutablePartialFieldStateProvider
