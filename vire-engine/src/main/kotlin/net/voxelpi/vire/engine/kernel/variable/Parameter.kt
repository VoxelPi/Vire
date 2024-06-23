package net.voxelpi.vire.engine.kernel.variable

import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A kernel parameter, they allow the configuration of fundamental kernel features, like the presence and size of non parameter variables.
 * The value of a parameter can only be set during the creation of a kernel variant and remains immutable after that.
 */
public data class Parameter<T> internal constructor(
    override val name: String,
    override val type: KType,
    public val initialization: () -> T,
    override val constraint: VariableConstraint<T>,
) : ScalarVariable<T>, ConstrainedVariable<T>

/**
 * A builder for a kernel parameter.
 *
 * @property name The name of the parameter.
 * @property type The type of the parameter.
 */
public class ParameterBuilder<T> internal constructor(
    public val name: String,
    public val type: KType,
) {

    /**
     * The initialization of the parameter.
     */
    public lateinit var initialization: () -> T

    /**
     * The constraint of the parameter.
     * The default value is [VariableConstraint.Always].
     */
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

/**
 * Creates a new parameter with the given [name] and type [T] using the given [lambda].
 */
public inline fun <reified T> createParameter(name: String, noinline lambda: ParameterBuilder<T>.() -> Unit = {}): Parameter<T> {
    return createParameter(name, typeOf<T>(), lambda)
}

/**
 * Creates a new parameter with the given [name] and [type] using the given [lambda].
 */
public fun <T> createParameter(name: String, type: KType, lambda: ParameterBuilder<T>.() -> Unit = {}): Parameter<T> {
    val builder = ParameterBuilder<T>(name, type)
    builder.lambda()
    return Parameter(name, type, builder.buildInitialization(), builder.constraint)
}
