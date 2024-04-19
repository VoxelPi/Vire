package net.voxelpi.vire.engine.circuit.kernel.variable

/**
 * An abstract kernel variable.
 */
public sealed interface Variable {

    /**
     * The name of the kernel variable.
     */
    public val name: String
}

/**
 * An abstract io variable.
 */
public sealed interface IOVector : Variable

/**
 * An abstract io variable.
 */
public data class IOVectorElement(
    val vector: IOVector,
    val index: Int,
) {

    /**
     * The name of the variable. Consists of the name of the vector name and the element index.
     */
    public val name: String
        get() = "${vector.name}[$index]"
}
