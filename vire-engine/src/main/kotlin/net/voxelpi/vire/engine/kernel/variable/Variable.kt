package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProviderWrapper
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
     * The description of the kernel variable.
     */
    public val description: String

    /**
     * Returns if the given [type] is valid for the variable.
     */
    public fun isValidType(type: KType): Boolean {
        return type.isSubtypeOf(this.type)
    }

    /**
     * Returns if the given [value] is valid for the variable.
     */
    public fun isValidValue(value: T): Boolean {
        return true
    }

    /**
     * Returns if the given [value] is valid for the variable.
     */
    public fun isValidTypeAndValue(value: Any?): Boolean {
        return isInstanceOfType(value, type)
    }
}

/**
 * A scalar variable.
 */
public sealed interface ScalarVariable<T> : Variable<T>

/**
 * A vector variable.
 * Represents a vector of variables of the same type, which size is set during the configuration phase of a kernel.
 */
public sealed interface VectorVariable<T> : Variable<T> {

    /**
     * The initial size of the vector.
     * Note that the size of a vector variable can be set to a different value during the configuration of a kernel.
     */
    public val size: VectorSizeInitializationContext.() -> Int

    /**
     * Returns if the give [size] is valid for this vector variable.
     */
    public fun isValidSize(size: Int): Boolean {
        return size >= 0
    }

    /**
     * Getter for the element at index [index] in the vector.
     */
    public operator fun get(index: Int): VectorVariableElement<T>
}

/**
 * An element of a vector variable.
 */
public sealed interface VectorVariableElement<T> : Variable<T> {

    /**
     * The vector variable to which this element belongs.
     */
    public val vector: VectorVariable<T>

    /**
     * The index of the element in the vector.
     */
    public val index: Int

    override val name: String
        get() = "${vector.name}[$index]"

    override val type: KType
        get() = vector.type

    override val description: String
        get() = vector.description
}

/**
 * A variable that has a [VariableConstraint] for its value.
 * The variable only accepts values that fulfil the constraint.
 */
public sealed interface ConstrainedVariable<T> : Variable<T> {

    /**
     * The constrained for the value of the variable.
     */
    public val constraint: VariableConstraint<T>

    /**
     * Returns if the given [value] is valid for the variable.
     */
    override fun isValidValue(value: T): Boolean {
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

/**
 * A vector variable that has a [VariableConstraint] for its size.
 * The vector size must fulfil the constraint.
 */
public sealed interface ConstrainedSizeVectorVariable<T> : VectorVariable<T> {

    /**
     * The constrained for the size of the vector variable.
     */
    public val sizeConstraint: VariableConstraint<Int>

    override fun isValidSize(size: Int): Boolean {
        return size >= 0 && sizeConstraint.isValidValue(size)
    }
}

/**
 * The initialization context for the size of a vector variable.
 */
public class VectorSizeInitializationContext internal constructor(
    override val parameterStateProvider: ParameterStateProvider,
) : ParameterStateProviderWrapper

/**
 * A variable that can be declared during the configuration of the kernel and be therefore dependent on the kernel variant.
 */
public sealed interface VariantVariable<T> : Variable<T>
