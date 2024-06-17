package net.voxelpi.vire.engine.kernel.variable

import kotlin.reflect.KType
import kotlin.reflect.typeOf

public data class Parameter<T> internal constructor(
    override val name: String,
    override val type: KType,
    public val initialization: () -> T,
    override val constraint: VariableConstraint<T>,
) : ScalarVariable<T>, ConstrainedVariable<T>

public class ParameterBuilder<T> internal constructor(
    public val name: String,
    public val type: KType,
) {

    public lateinit var initialization: () -> T

    public var constraint: VariableConstraint<T> = VariableConstraint.Always

    @Suppress("UNCHECKED_CAST")
    internal fun buildInitialization(): () -> T {
        // Check if the initialization has been set.
        val initialized = ::initialization.isInitialized
        if (initialized) {
            return initialization
        }

        // Return null initialization if the type allows it.
        if (type.isMarkedNullable) {
            return {
                null as T
            }
        }

        // Otherwise throw.
        throw IllegalArgumentException("Missing initialization for parameter \"$name\"")
    }
}

public inline fun <reified T> createParameter(name: String, noinline lambda: ParameterBuilder<T>.() -> Unit = {}): Parameter<T> {
    return createParameter(name, typeOf<T>(), lambda)
}

public fun <T> createParameter(name: String, type: KType, lambda: ParameterBuilder<T>.() -> Unit = {}): Parameter<T> {
    val builder = ParameterBuilder<T>(name, type)
    builder.lambda()
    return Parameter(name, type, builder.buildInitialization(), builder.constraint)
}
