package net.voxelpi.vire.engine.kernel.variable

public sealed interface Input : IOVariable

public data class InputScalar internal constructor(
    override val name: String,
) : IOScalarVariable, Input

public data class InputVector internal constructor(
    override val name: String,
    override val size: VectorSizeInitializationContext.() -> Int,
) : IOVectorVariable, Input {

    override fun get(index: Int): InputVectorElement {
        return InputVectorElement(this, index)
    }
}

public data class InputVectorElement internal constructor(
    override val vector: InputVector,
    override val index: Int,
) : IOVectorVariableElement, Input

public class InputScalarBuilder internal constructor(
    public val name: String,
)

public class InputVectorBuilder internal constructor(
    public val name: String,
) {

    public var size: VectorSizeInitializationContext.() -> Int = { 0 }
}

public fun createInput(name: String, lambda: InputScalarBuilder.() -> Unit = {}): InputScalar {
    val builder = InputScalarBuilder(name)
    builder.lambda()
    return InputScalar(name)
}

public fun createInputVector(name: String, lambda: InputVectorBuilder.() -> Unit = {}): InputVector {
    val builder = InputVectorBuilder(name)
    builder.lambda()
    return InputVector(name, builder.size)
}
