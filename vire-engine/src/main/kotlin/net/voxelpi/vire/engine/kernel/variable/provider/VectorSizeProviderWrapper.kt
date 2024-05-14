package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable

internal interface VectorSizeProviderWrapper : VectorSizeProvider {

    val vectorSizeProvider: VectorSizeProvider

    override val variableProvider: VariableProvider
        get() = vectorSizeProvider.variableProvider

    override fun size(vector: VectorVariable<*>): Int {
        return vectorSizeProvider.size(vector)
    }

    override fun size(vectorName: String): Int {
        return vectorSizeProvider.size(vectorName)
    }
}

internal interface MutableVectorSizeProviderWrapper : VectorSizeProviderWrapper, MutableVectorSizeProvider {

    override val vectorSizeProvider: MutableVectorSizeProvider

    override fun resize(vector: VectorVariable<*>, size: Int) {
        vectorSizeProvider.resize(vector, size)
    }

    override fun resize(vectorName: String, size: Int) {
        vectorSizeProvider.resize(vectorName, size)
    }
}
