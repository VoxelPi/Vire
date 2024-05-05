package net.voxelpi.vire.engine.kernel.variable

public sealed interface Output : IOVariable

public data class OutputScalar internal constructor(
    override val name: String,
) : IOScalarVariable, Output

public data class OutputVector internal constructor(
    override val name: String,
    override val size: VectorVariableSize,
) : IOVectorVariable, Output {

    override fun get(index: Int): IOVectorVariableElement {
        return OutputVectorElement(this, index)
    }
}

public data class OutputVectorElement internal constructor(
    override val vector: OutputVector,
    override val index: Int,
) : IOVectorVariableElement, Output

/**
 * Creates a new scalar output variable with the given [name].
 */
public fun output(
    name: String,
): OutputScalar {
    return OutputScalar(name)
}

/**
 * Creates a new vector output variable with the given [name] and [size].
 */
public fun output(
    name: String,
    size: VectorVariableSize,
): OutputVector {
    return OutputVector(name, size)
}

/**
 * Creates a new vector output variable with the given [name] and default [size].
 */
public fun output(
    name: String,
    size: Int,
): OutputVector = output(name, VectorVariableSize.Value(size))

/**
 * Creates a new vector output variable with the given [name] using the given [parameter] as default size.
 */
public fun output(
    name: String,
    parameter: Parameter<Int>,
): OutputVector = output(name, VectorVariableSize.Parameter(parameter))
