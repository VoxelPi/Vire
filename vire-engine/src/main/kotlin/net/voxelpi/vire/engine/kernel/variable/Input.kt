package net.voxelpi.vire.engine.kernel.variable

public sealed interface Input : IOVariable

public data class InputScalar internal constructor(
    override val name: String,
) : IOScalarVariable, Input

public data class InputVector internal constructor(
    override val name: String,
    override val size: VectorVariableSize,
) : IOVectorVariable, Input {

    override fun get(index: Int): IOVectorVariableElement {
        return InputVectorElement(this, index)
    }
}

public data class InputVectorElement internal constructor(
    override val vector: InputVector,
    override val index: Int,
) : IOVectorVariableElement, Input

/**
 * Creates a new scalar input variable with the given [name].
 */
public fun input(
    name: String,
): InputScalar {
    return InputScalar(name)
}

/**
 * Creates a new vector input variable with the given [name] and [size].
 */
public fun input(
    name: String,
    size: VectorVariableSize,
): InputVector {
    return InputVector(name, size)
}

/**
 * Creates a new vector input variable with the given [name] and default [size].
 */
public fun input(
    name: String,
    size: Int,
): InputVector = input(name, VectorVariableSize.Value(size))

/**
 * Creates a new vector input variable with the given [name] using the given [parameter] as default size.
 */
public fun input(
    name: String,
    parameter: Parameter<Int>,
): InputVector = input(name, VectorVariableSize.Parameter(parameter))
