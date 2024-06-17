package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.util.isInstanceOfType
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

/**
 * An abstract kernel variable.
 */
public sealed interface Variable<T> {

    /**
     * The name of the kernel variable.
     */
    public val name: String

    /**
     * The type of the kernel variable.
     */
    public val type: KType

    /**
     * Returns if the given [type] is valid for the variable.
     */
    public fun isValidType(type: KType): Boolean {
        return type.isSubtypeOf(this.type)
    }

    /**
     * Returns if the given [value] is valid for the variable.
     */
    public fun isValidTypeAndValue(value: Any?): Boolean {
        return isInstanceOfType(value, type)
    }
}

public sealed interface ScalarVariable<T> : Variable<T>

public sealed interface VectorVariable<T> : Variable<T> {

    public val size: VectorSizeInitializationContext.() -> Int

    public operator fun get(index: Int): VectorVariableElement<T>
}

public sealed interface VectorVariableElement<T> : Variable<T> {

    public val vector: VectorVariable<T>

    public val index: Int

    override val name: String
        get() = "${vector.name}[$index]"

    override val type: KType
        get() = vector.type
}

public sealed interface ConstrainedVariable<T> : Variable<T> {

    public val constraint: VariableConstraint<T>

    /**
     * Returns if the given [value] is valid for the variable.
     */
    public fun isValidValue(value: T): Boolean {
        return constraint.isValidValue(value)
    }

    /**
     * Returns if the given [value] is valid for the variable.
     */
    @Suppress("UNCHECKED_CAST")
    override fun isValidTypeAndValue(value: Any?): Boolean {
        if (!isInstanceOfType(value, type)) {
            return false
        }
        return isValidValue(value as T)
    }
}

public class VectorSizeInitializationContext internal constructor(
    private val parameterStateProvider: ParameterStateProvider,
) : ParameterStateProvider {

    override val variableProvider: VariableProvider
        get() = parameterStateProvider.variableProvider

    override fun <T> get(parameter: Parameter<T>): T {
        return parameterStateProvider[parameter]
    }
}

/**
 * A variable that can be declared during the configuration of the kernel and be therefore dependent on the kernel variant.
 */
public sealed interface VariantVariable<T> : Variable<T>
