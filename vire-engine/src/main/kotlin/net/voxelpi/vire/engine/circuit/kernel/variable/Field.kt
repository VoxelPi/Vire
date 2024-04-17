package net.voxelpi.vire.engine.circuit.kernel.variable

import net.voxelpi.vire.engine.util.isInstanceOfType
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public interface Field<T> : Variable {

    /**
     * The name of the variable.
     */
    override val name: String

    /**
     * The type of the variable.
     */
    public val type: KType

    /**
     * The initialization of the field.
     */
    public val initialization: VariableInitialization<T>

    /**
     * Returns if the given [value] is valid for the field.
     */
    public fun isValidValue(value: Any?): Boolean
}

/**
 * Creates a new field with the given [name] that is initialized to the value of [initialization].
 */
public inline fun <reified T> field(
    name: String,
    initialization: T,
): Field<T> {
    return field(
        name,
        typeOf<T>(),
        initialization,
    )
}

/**
 * Creates a new field with the given [name] that is initialized to the value of [initialization].
 */
public fun <T> field(
    name: String,
    type: KType,
    initialization: T,
): Field<T> {
    return FieldImpl(
        name,
        type,
        VariableInitialization.constant(initialization),
    )
}

/**
 * Creates a new field with the given [name] that is initialized to the value provided by [initialization].
 */
public inline fun <reified T> field(
    name: String,
    noinline initialization: () -> T,
): Field<T> {
    return field(
        name,
        typeOf<T>(),
        initialization,
    )
}

/**
 * Creates a new field with the given [name] that is initialized to the value provided by [initialization].
 */
public fun <T> field(
    name: String,
    type: KType,
    initialization: () -> T,
): Field<T> {
    return FieldImpl(
        name,
        type,
        VariableInitialization.dynamic(initialization),
    )
}

internal data class FieldImpl<T>(
    override val name: String,
    override val type: KType,
    override val initialization: VariableInitialization<T>,
) : Field<T> {

    override fun isValidValue(value: Any?): Boolean {
        return isInstanceOfType(value, type)
    }
}
