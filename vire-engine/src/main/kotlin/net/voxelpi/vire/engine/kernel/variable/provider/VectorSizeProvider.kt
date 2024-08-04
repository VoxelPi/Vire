package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.patch.VectorSizePatch
import net.voxelpi.vire.engine.kernel.variable.storage.VectorSizeMap

/**
 * A type that provides access to the size of some of the registered vector variables.
 */
public interface PartialVectorSizeProvider {

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

    /**
     * Checks if the given [vector] has a set size.
     */
    public fun hasSize(vector: VectorVariable<*>): Boolean

    /**
     * Checks if the vector with the given [vectorName] has a set size.
     */
    public fun hasSize(vectorName: String): Boolean

    /**
     * Checks if all registered vectors have a set size.
     */
    public fun allVectorSizesSet(): Boolean
}

/**
 * A type that provides mutable access to the size of some of the registered vector variables.
 */
public interface MutablePartialVectorSizeProvider : PartialVectorSizeProvider {

    /**
     * Changes the size of the given [vector] to the given [size].
     */
    public fun resize(vector: VectorVariable<*>, size: Int)

    /**
     * Changes the size of the vector with the given [vectorName] to the given [size].
     */
    public fun resize(vectorName: String, size: Int)

    /**
     * Copies all values present in the given [provider] to this provider.
     */
    public fun applyVectorSizePatch(provider: PartialVectorSizeProvider) {
        for (variable in provider.variableProvider.vectorVariables().filter(provider::hasSize)) {
            resize(variable, provider.size(variable))
        }
    }

    /**
     * Copies all values present in the given [map] to this provider.
     */
    public fun applyVectorSizePatch(map: VectorSizeMap) {
        applyVectorSizePatch(VectorSizePatch(variableProvider, map))
    }
}

/**
 * A type that provides access to the size of all registered vector variables.
 */
public interface VectorSizeProvider : PartialVectorSizeProvider {

    override fun hasSize(vector: VectorVariable<*>): Boolean = vector in variableProvider.vectorVariables()

    override fun hasSize(vectorName: String): Boolean = variableProvider.hasVectorVariable(vectorName)

    override fun allVectorSizesSet(): Boolean = true
}

/**
 * A type that provides mutable access to the size of all registered vector variables.
 */
public interface MutableVectorSizeProvider : VectorSizeProvider, MutablePartialVectorSizeProvider
