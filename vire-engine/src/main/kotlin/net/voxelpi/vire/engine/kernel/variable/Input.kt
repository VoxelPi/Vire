package net.voxelpi.vire.engine.kernel.variable

public sealed interface Input : IOVariable

public data class InputScalar(
    override val name: String,
) : IOScalarVariable, Input

public data class InputVector(
    override val name: String,
    override val size: VectorVariableSize,
) : IOVectorVariable, Input {

    override fun get(index: Int): IOVectorVariableElement {
        return InputVectorElement(this, index)
    }
}

public data class InputVectorElement(
    override val vector: InputVector,
    override val index: Int,
) : IOVectorVariableElement, Input
