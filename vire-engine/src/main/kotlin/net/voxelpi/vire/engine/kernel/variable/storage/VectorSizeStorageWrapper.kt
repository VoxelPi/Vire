package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.provider.MutableVectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider

internal interface VectorSizeStorageWrapper : VectorSizeProvider {

    val vectorSizeStorage: VectorSizeStorage

    override val variableProvider: VariableProvider
        get() = vectorSizeStorage.variableProvider

    override fun size(vector: VectorVariable<*>): Int {
        return vectorSizeStorage.size(vector)
    }

    override fun size(vectorName: String): Int {
        return vectorSizeStorage.size(vectorName)
    }
}

internal interface MutableVectorSizeStorageWrapper : VectorSizeStorageWrapper, MutableVectorSizeProvider {

    override val vectorSizeStorage: MutableVectorSizeStorage

    override fun resize(vector: VectorVariable<*>, size: Int) {
        vectorSizeStorage.resize(vector, size)
    }

    override fun resize(vectorName: String, size: Int) {
        vectorSizeStorage.resize(vectorName, size)
    }
}
