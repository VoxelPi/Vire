package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.VectorVariableSizeProvider

internal interface KernelVariantWrapper : ParameterStateProvider, VectorVariableSizeProvider {

    val kernelVariant: KernelVariant

    val kernel: Kernel
        get() = kernelVariant.kernel

    override fun <T> get(parameter: Parameter<T>): T = kernelVariant[parameter]

    override fun size(vector: VectorVariable<*>): Int = kernelVariant.size(vector)

    override fun size(vectorName: String): Int = kernelVariant.size(vectorName)
}
