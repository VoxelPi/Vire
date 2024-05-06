package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.util.isInstanceOfType
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

public data class Setting<T> internal constructor(
    override val name: String,
    override val type: KType,
    public val initialization: () -> T,
    public val constraint: VariableConstraint<T>,
) : ScalarVariable<T>, VariantVariable<T> {

    /**
     * Returns if the given [value] is valid for the setting.
     */
    public fun isValidValue(value: T): Boolean {
        return constraint.isValidValue(value)
    }

    /**
     * Returns if the given [type] is valid for the setting.
     */
    public fun isValidType(type: KType): Boolean {
        return type.isSubtypeOf(this.type)
    }

    /**
     * Returns if the given [value] is valid for the setting.
     */
    @Suppress("UNCHECKED_CAST")
    public fun isValidTypeAndValue(value: Any?): Boolean {
        if (!isInstanceOfType(value, type)) {
            return false
        }
        return isValidValue(value as T)
    }
}

/**
 * Creates a new unconstrained setting with the given [name], [initialization] and [constraint].
 */
public inline fun <reified T> setting(
    name: String,
    noinline initialization: () -> T,
    constraint: VariableConstraint<T> = VariableConstraint.Always,
): Setting<T> = setting(name, typeOf<T>(), initialization, constraint)

/**
 * Creates a new unconstrained setting with the given [name], [initialization] and [constraintBuilder].
 * The [constraintBuilder] is used to create an all-constrained, that means a value must be valid for all the defined constrains.
 */
public inline fun <reified T> setting(
    name: String,
    noinline initialization: () -> T,
    noinline constraintBuilder: AllVariableConstraintBuilder<T>.() -> Unit,
): Setting<T> = setting(name, typeOf<T>(), initialization, constraintBuilder)

/**
 * Creates a new unconstrained setting with the given [name], [type], [initialization] and [constraint].
 */
public fun <T> setting(
    name: String,
    type: KType,
    initialization: () -> T,
    constraint: VariableConstraint<T> = VariableConstraint.Always,
): Setting<T> = Setting(name, type, initialization, constraint)

/**
 * Creates a new unconstrained setting with the given [name], [type], [initialization] and [constraintBuilder].
 * The [constraintBuilder] is used to create an all-constrained, that means a value must be valid for all the defined constrains.
 */
public fun <T> setting(
    name: String,
    type: KType,
    initialization: () -> T,
    constraintBuilder: AllVariableConstraintBuilder<T>.() -> Unit,
): Setting<T> = Setting(name, type, initialization, AllVariableConstraintBuilder<T>().apply(constraintBuilder).build())
