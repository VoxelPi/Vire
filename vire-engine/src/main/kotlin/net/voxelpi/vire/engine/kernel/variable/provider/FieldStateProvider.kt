package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.patch.FieldStatePatch
import net.voxelpi.vire.engine.kernel.variable.storage.FieldStateMap

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
    public operator fun <T> get(field: Field<T>): T

    /**
     * Returns if the given field has a set value.
     */
    public fun hasValue(field: Field<*>): Boolean

    /**
     * Checks if all registered fields have a set value.
     */
    public fun allFieldsSet(): Boolean
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

    /**
     * Copies all values present in the given [provider] to this provider.
     */
    @Suppress("UNCHECKED_CAST")
    public fun applyFieldStatePatch(provider: PartialFieldStateProvider) {
        for (field in provider.variableProvider.fields().filter(provider::hasValue)) {
            this[(field as Field<Any?>)] = provider[field]
        }
    }

    /**
     * Copies all values present in the given [map] to this provider.
     */
    public fun applyFieldStatePatch(map: FieldStateMap) {
        applyFieldStatePatch(FieldStatePatch(variableProvider, map))
    }
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

    override fun hasValue(field: Field<*>): Boolean = field in variableProvider.fields()

    override fun allFieldsSet(): Boolean = true
}

/**
 * A type that provides mutable access to the state of all registered field variables.
 */
public interface MutableFieldStateProvider : FieldStateProvider, MutablePartialFieldStateProvider
