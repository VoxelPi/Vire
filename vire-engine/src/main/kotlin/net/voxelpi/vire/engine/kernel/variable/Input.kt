package net.voxelpi.vire.engine.kernel.variable

public sealed interface Input : IOVariable

/**
 * A kernel input scalar, they are used to transfer a single [net.voxelpi.vire.engine.LogicState] from a circuit network into a kernel.
 * Their value can only be read in kernel updates and cannot be modified by the kernel itself.
 */
public data class InputScalar internal constructor(
    override val name: String,
    override val description: String,
) : IOScalarVariable, Input

/**
 * A kernel input vector, they are used to transfer multiple [net.voxelpi.vire.engine.LogicState] from a circuit network into a kernel.
 * Their value can only be read in kernel updates and cannot be modified by the kernel itself.
 */
public data class InputVector internal constructor(
    override val name: String,
    override val size: VectorSizeInitializationContext.() -> Int,
    override val description: String,
) : IOVectorVariable, Input {

    override fun get(index: Int): InputVectorElement {
        return InputVectorElement(this, index)
    }
}

/**
 * An element of an input vector.
 */
public data class InputVectorElement internal constructor(
    override val vector: InputVector,
    override val index: Int,
) : IOVectorVariableElement, Input

/**
 * A build for an input scalar.
 *
 * @property name The name of the input scalar.
 */
public class InputScalarBuilder internal constructor(
    public val name: String,
) {

    /**
     * The description of the input scalar.
     */
    public var description: String = ""
}

/**
 * A build for an input vector.
 *
 * @property name The name of the input vector.
 */
public class InputVectorBuilder internal constructor(
    public val name: String,
) {

    /**
     * The initial size of the input vector.
     * Note that the size of a vector variable can be set to a different value during the configuration of a kernel.
     */
    public var size: VectorSizeInitializationContext.() -> Int = { 0 }

    /**
     * The description of the input vector.
     */
    public var description: String = ""
}

/**
 * Creates a new input scalar with the given [name] using the given [lambda].
 */
public fun createInput(name: String, lambda: InputScalarBuilder.() -> Unit = {}): InputScalar {
    val builder = InputScalarBuilder(name)
    builder.lambda()
    return InputScalar(name, builder.description)
}

/**
 * Creates a new input vector with the given [name] using the given [lambda].
 */
public fun createInputVector(name: String, lambda: InputVectorBuilder.() -> Unit = {}): InputVector {
    val builder = InputVectorBuilder(name)
    builder.lambda()
    return InputVector(name, builder.size, builder.description)
}
