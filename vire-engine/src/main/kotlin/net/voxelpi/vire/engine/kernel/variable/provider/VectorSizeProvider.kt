package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable

/**
 * A type that provides ways to access the size of a vector variable.
 */
public interface VectorSizeProvider {

    /**
     * The variable provider for which the vector sizes should be provided.
     */
    public val variableProvider: VariableProvider

    /**
     * Returns the size of the given [vector].
     */
    public fun size(vector: VectorVariable<*>): Int

    /**
     * Returns the size of the vector with the given [vectorName].
     */
    public fun size(vectorName: String): Int
}

/**
 * A type that provides ways to access and modify the size of a vector variable.
 */
public interface MutableVectorSizeProvider : VectorSizeProvider {

    /**
     * Changes the size of the given [vector] to the given [size].
     */
    public fun resize(vector: VectorVariable<*>, size: Int)

    /**
     * Changes the size of the vector with the given [vectorName] to the given [size].
     */
    public fun resize(vectorName: String, size: Int)
}
