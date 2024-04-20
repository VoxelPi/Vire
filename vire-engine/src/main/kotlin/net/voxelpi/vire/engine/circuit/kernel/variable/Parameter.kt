package net.voxelpi.vire.engine.circuit.kernel.variable

import kotlin.reflect.KType

public interface Parameter<T> : Variable {

    override val name: String

    public val type: KType

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
