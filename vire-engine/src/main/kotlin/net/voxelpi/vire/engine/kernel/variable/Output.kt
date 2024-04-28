package net.voxelpi.vire.engine.kernel.variable

public sealed interface Output : IOVariable

public data class OutputScalar(
    override val name: String,
) : IOScalarVariable, Output

public data class OutputVector(
    override val name: String,
    override val size: VectorVariableSize,
) : IOVectorVariable, Output {

    override fun get(index: Int): IOVectorVariableElement {
        return OutputVectorElement(this, index)
    }
}

public data class OutputVectorElement(
    override val vector: OutputVector,
    override val index: Int,
) : IOVectorVariableElement, Output
