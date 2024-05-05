package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.util.isInstanceOfType
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

public data class Field<T> internal constructor(
    override val name: String,
    override val type: KType,
    public val initialization: () -> T,
) : ScalarVariable<T> {

    /**
     * Returns if the given [type] is valid for the parameter.
     */
    public fun isValidType(type: KType): Boolean {
        return type.isSubtypeOf(this.type)
    }

    /**
     * Returns if the given [value] is valid for the parameter.
     */
    public fun isValidTypeAndValue(value: Any?): Boolean {
        return isInstanceOfType(value, type)
    }
}

/**
 * Creates a new field with the given [name] that is initialized to the value provided by [initialization].
 */
public inline fun <reified T> field(
    name: String,
    noinline initialization: () -> T,
): Field<T> {
    return field(name, typeOf<T>(), initialization)
}

/**
 * Creates a new field with the given [name] that is initialized to the value provided by [initialization].
 */
public fun <T> field(
    name: String,
    type: KType,
    initialization: () -> T,
): Field<T> {
    return Field(name, type, initialization)
}
