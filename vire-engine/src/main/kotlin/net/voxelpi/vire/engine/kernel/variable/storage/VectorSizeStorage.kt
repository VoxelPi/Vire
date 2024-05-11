package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.provider.MutableVectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider

internal typealias VectorSizeMap = Map<String, Int>

internal typealias MutableVectorSizeMap = MutableMap<String, Int>

internal interface VectorSizeStorage : VectorSizeProvider {

    override val variableProvider: VariableProvider

    val data: VectorSizeMap

    fun copy(): VectorSizeStorage

    fun mutableCopy(): MutableVectorSizeStorage

    override fun size(vector: VectorVariable<*>): Int = size(vector.name)

    override fun size(vectorName: String): Int {
        // Check that the vector variable is defined on the kernel.
        require(variableProvider.hasVectorVariable(vectorName))

        // Return the size of the vector variable from the map.
        return data[vectorName]!!
    }
}

internal class MutableVectorSizeStorage(
    override val variableProvider: VariableProvider,
    override val data: MutableVectorSizeMap,
) : VectorSizeStorage, MutableVectorSizeProvider {

    override fun copy(): VectorSizeStorage = mutableCopy()

    override fun mutableCopy(): MutableVectorSizeStorage {
        return MutableVectorSizeStorage(variableProvider, data.toMutableMap())
    }

    override fun resize(vector: VectorVariable<*>, size: Int) = resize(vector.name, size)

    override fun resize(vectorName: String, size: Int) {
        // Check that the vector variable is defined on the kernel.
        require(variableProvider.hasVectorVariable(vectorName))

        // Check that the size of the vector variable is greater than 0.
        require(size >= 0) { "The size of a vector variable must be greater than or equal to zero" }

        // Modify the size of the vector variable in the map.
        data[vectorName] = size
    }
}

internal fun vectorSizeStorage(variableProvider: VariableProvider, data: VectorSizeMap): VectorSizeStorage {
    return mutableVectorSizeStorage(variableProvider, data)
}

internal fun vectorSizeStorage(variableProvider: VariableProvider, dataProvider: VectorSizeProvider): VectorSizeStorage {
    return mutableVectorSizeStorage(variableProvider, dataProvider)
}

internal fun mutableVectorSizeStorage(variableProvider: VariableProvider, data: VectorSizeMap): MutableVectorSizeStorage {
    val processedData: MutableVectorSizeMap = mutableMapOf()
    for (vector in variableProvider.vectorVariables()) {
        // Check that the parameter has an assigned value.
        require(vector.name in data) { "No size provided for the vector ${vector.name}" }

        // Get the size from the map.
        val size = data[vector.name]!!

        // Put value into map.
        processedData[vector.name] = size
    }
    return MutableVectorSizeStorage(variableProvider, processedData)
}

internal fun mutableVectorSizeStorage(variableProvider: VariableProvider, dataProvider: VectorSizeProvider): MutableVectorSizeStorage {
    val processedData: MutableVectorSizeMap = mutableMapOf()
    for (vector in variableProvider.vectorVariables()) {
        // Check that the parameter has an assigned value.
        require(dataProvider.variableProvider.hasVariable(vector)) { "No size provided for the vector ${vector.name}" }

        // Get the size from the provider.
        val size = dataProvider.size(vector.name)

        // Put value into map.
        processedData[vector.name] = size
    }
    return MutableVectorSizeStorage(variableProvider, processedData)
}
