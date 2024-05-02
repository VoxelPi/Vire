package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.SettingStateMap
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.VectorVariableSizeProvider

public interface KernelInstance : ParameterStateProvider, VectorVariableSizeProvider, SettingStateProvider, OutputStateProvider {

    public val kernelVariant: KernelVariant

    public val kernel: Kernel
        get() = kernelVariant.kernel

    override fun <T> get(parameter: Parameter<T>): T = kernelVariant[parameter]

    override fun size(vector: VectorVariable<*>): Int = kernelVariant.size(vector)

    override fun size(vectorName: String): Int = kernelVariant.size(vectorName)
}

internal class KernelInstanceImpl(
    kernelVariant: KernelVariantImpl,
    override val variableStates: Map<String, Any?>,
) : KernelInstance, SettingStateMap {

    override val kernelVariant: KernelVariantImpl = kernelVariant.clone()

    override val kernel: KernelImpl
        get() = kernelVariant.kernel

    override fun get(output: OutputScalar): LogicState {
        TODO("Not yet implemented")
    }

    override fun get(outputVector: OutputVector): Array<LogicState> {
        TODO("Not yet implemented")
    }

    override fun get(outputVector: OutputVector, index: Int): LogicState {
        TODO("Not yet implemented")
    }
}
