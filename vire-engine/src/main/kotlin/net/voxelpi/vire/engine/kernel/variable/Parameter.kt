package net.voxelpi.vire.engine.kernel.variable

import kotlin.reflect.KType
import kotlin.reflect.typeOf

public data class Parameter<T> internal constructor(
    override val name: String,
    override val type: KType,
    public val initialization: () -> T,
    override val constraint: VariableConstraint<T>,
) : ScalarVariable<T>, ConstrainedVariable<T>

/**
 * Creates a new parameter with the given [name], [initialization] and [constraint].
 */
public inline fun <reified T> createParameter(
    name: String,
    noinline initialization: () -> T,
    constraint: VariableConstraint<T> = VariableConstraint.Always,
): Parameter<T> = createParameter(name, typeOf<T>(), initialization, constraint)

/**
 * Creates a new parameter with the given [name], [initialization] and [constraintBuilder].
 * The [constraintBuilder] is used to create an all-constrained, that means a value must be valid for all the defined constrains.
 */
public inline fun <reified T> createParameter(
    name: String,
    noinline initialization: () -> T,
    noinline constraintBuilder: AllVariableConstraintBuilder<T>.() -> Unit,
): Parameter<T> = createParameter(name, typeOf<T>(), initialization, constraintBuilder)

/**
 * Creates a new parameter with the given [name], [type], [initialization] and [constraint].
 */
public fun <T> createParameter(
    name: String,
    type: KType,
    initialization: () -> T,
    constraint: VariableConstraint<T> = VariableConstraint.Always,
): Parameter<T> = Parameter(name, type, initialization, constraint)

/**
 * Creates a new parameter with the given [name], [type], [initialization] and [constraintBuilder].
 * The [constraintBuilder] is used to create an all-constrained, that means a value must be valid for all the defined constrains.
 */
public fun <T> createParameter(
    name: String,
    type: KType,
    initialization: () -> T,
    constraintBuilder: AllVariableConstraintBuilder<T>.() -> Unit,
): Parameter<T> = Parameter(name, type, initialization, AllVariableConstraintBuilder<T>().apply(constraintBuilder).build())
