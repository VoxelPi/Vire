package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.provider.MutableVectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider

public typealias VectorSizeMap = Map<String, Int>

public typealias MutableVectorSizeMap = MutableMap<String, Int>

/**
 * A collection that stores the sizes of all vector variables of the given [variableProvider].
 */
public open class VectorSizeStorage(
    final override val variableProvider: VariableProvider,
    initialData: VectorSizeMap,
) : VectorSizeProvider {

    init {
        for ((vectorName, vectorSize) in initialData) {
            val vector = variableProvider.vectorVariable(vectorName)
                ?: throw IllegalStateException("Data specified for unknown vector variable \"$vectorName\".")

            require(vector.isValidSize(vectorSize)) { "Invalid size specified for the vector variable \"$vectorName\"." }
        }

        val missingVariables = variableProvider.vectorVariables().map { it.name }.filter { it !in initialData.keys }
        require(missingVariables.isEmpty()) {
            "Missing sizes for the following vector variables ${missingVariables.joinToString(", ") { "\"${it}\"" } }"
        }
    }

    protected open val data: VectorSizeMap = initialData.toMap()

    public constructor(variableProvider: VariableProvider, initialData: VectorSizeProvider) : this(
        variableProvider,
        variableProvider.vectorVariables().filter { initialData.hasSize(it) }.associate { it.name to initialData.size(it) }
    )

    /**
     * Creates a copy of this storage.
     */
    public fun copy(): VectorSizeStorage {
        return VectorSizeStorage(variableProvider, data)
    }

    /**
     * Creates a mutable copy of this storage.
     */
    public fun mutableCopy(): MutableVectorSizeStorage {
        return MutableVectorSizeStorage(variableProvider, data)
    }

    override fun size(vector: VectorVariable<*>): Int {
        // Check that a vector with the given name exists.
        require(variableProvider.hasVectorVariable(vector)) { "Unknown vector variable ${vector.name}" }

        // Return the size of the vector.
        return data[vector.name]!!
    }

    override fun size(vectorName: String): Int {
        // Check that a vector with the given name exists.
        require(variableProvider.hasVectorVariable(vectorName)) { "Unknown vector variable $vectorName" }

        // Return the size of the vector.
        return data[vectorName]!!
    }
}

/**
 * A mutable collection that stores the sizes of all vector variables of the given [variableProvider].
 */
public class MutableVectorSizeStorage(
    variableProvider: VariableProvider,
    initialData: VectorSizeMap,
) : VectorSizeStorage(variableProvider, initialData), MutableVectorSizeProvider {

    override val data: MutableVectorSizeMap = initialData.toMutableMap()

    public constructor(variableProvider: VariableProvider, initialData: VectorSizeProvider) : this(
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

    public fun unregister(vector: VectorVariable<*>) {
        require(variableProvider.hasVectorVariable(vector)) { "Unknown vector variable ${vector.name}" }

        data.remove(vector.name)
    }
}
