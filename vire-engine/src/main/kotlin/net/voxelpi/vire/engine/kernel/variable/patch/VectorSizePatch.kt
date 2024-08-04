package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.kernel.variable.UninitializedVariableException
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialVectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialVectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.storage.MutableVectorSizeMap
import net.voxelpi.vire.engine.kernel.variable.storage.VectorSizeMap
import net.voxelpi.vire.engine.kernel.variable.storage.VectorSizeStorage

internal open class VectorSizePatch(
    final override val variableProvider: VariableProvider,
    initialData: VectorSizeMap,
) : PartialVectorSizeProvider {

    init {
        for ((vectorName, size) in initialData) {
            val vector = variableProvider.vectorVariable(vectorName)
                ?: throw IllegalStateException("Data specified for unknown vector variable \"$vectorName\".")

            require(vector.isValidSize(size)) { "Invalid size $size specified for vector variable \"$vectorName\"." }
        }
    }

    protected open val data: VectorSizeMap = initialData.toMap()

    constructor(variableProvider: VariableProvider, initialData: PartialVectorSizeProvider) : this(
        variableProvider,
        variableProvider.vectorVariables().filter { initialData.hasSize(it) }.associate { it.name to initialData.size(it) }
    )

    fun copy(): VectorSizePatch {
        return VectorSizePatch(variableProvider, data)
    }

    fun mutableCopy(): MutableVectorSizePatch {
        return MutableVectorSizePatch(variableProvider, data)
    }

    override fun size(vector: VectorVariable<*>): Int {
        // Check that a vector with the given name exists.
        require(variableProvider.hasVectorVariable(vector)) { "Unknown vector variable ${vector.name}" }

        // Check that the size of the vector has been initialized.
        if (vector.name !in data) {
            throw UninitializedVariableException(vector)
        }

        // Return the size of the vector.
        return data[vector.name]!!
    }

    override fun size(vectorName: String): Int {
        // Check that a vector with the given name exists.
        require(variableProvider.hasVectorVariable(vectorName)) { "Unknown vector variable $vectorName" }

        // Return the size of the vector.
        return size(variableProvider.vectorVariable(vectorName)!!)
    }

    override fun hasSize(vector: VectorVariable<*>): Boolean {
        // Check that a vector with the given name exists.
        require(variableProvider.hasVectorVariable(vector)) { "Unknown vector variable ${vector.name}" }

        return vector.name in data
    }

    override fun hasSize(vectorName: String): Boolean {
        // Check that a vector with the given name exists.
        require(variableProvider.hasVectorVariable(vectorName)) { "Unknown vector variable $vectorName" }

        // Return the size of the vector.
        return vectorName in data
    }

    override fun allVectorSizesSet(): Boolean {
        return variableProvider.vectorVariables().all(this::hasSize)
    }

    /**
     * Creates a vector size state storage using the set data.
     * All vector variable must have a set size otherwise this operation fails.
     */
    fun createStorage(): VectorSizeStorage {
        return VectorSizeStorage(variableProvider, data)
    }
}

internal class MutableVectorSizePatch(
    variableProvider: VariableProvider,
    initialData: VectorSizeMap,
) : VectorSizePatch(variableProvider, initialData), MutablePartialVectorSizeProvider {

    override val data: MutableVectorSizeMap = initialData.toMutableMap()

    constructor(variableProvider: VariableProvider, initialData: PartialVectorSizeProvider) : this(
        variableProvider,
        variableProvider.vectorVariables().filter { initialData.hasSize(it) }.associate { it.name to initialData.size(it) }
    )

    override fun resize(vector: VectorVariable<*>, size: Int) {
        // Check that a vector with the given name exists.
        require(variableProvider.hasVectorVariable(vector)) { "Unknown vector variable ${vector.name}" }

        // Check that the size is valid for the specified vector variable.
        require(vector.isValidSize(size)) { "Size $size does not meet the requirements for the vector variable ${vector.name}" }

        // Update the size of the vector variable.
        data[vector.name] = size
    }

    override fun resize(vectorName: String, size: Int) {
        // Check that a vector with the given name exists.
        require(variableProvider.hasVectorVariable(vectorName)) { "Unknown vector variable $vectorName" }

        // Return the size of the vector.
        return resize(variableProvider.vectorVariable(vectorName)!!, size)
    }
}
