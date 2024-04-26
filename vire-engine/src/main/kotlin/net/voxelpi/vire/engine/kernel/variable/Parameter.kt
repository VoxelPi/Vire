package net.voxelpi.vire.engine.kernel.variable

import kotlin.reflect.KType

public interface Parameter<T> : Variable<T> {

    override val name: String

    override val type: KType

    public val initialization: VariableInitialization<T>

    /**
     * Returns if the given [value] is valid for the field.
     */
    public fun isValidValue(value: Any?): Boolean

    /**
     * Returns if the given [type] is valid for the field.
     */
    public fun isValidType(type: KType): Boolean
}
