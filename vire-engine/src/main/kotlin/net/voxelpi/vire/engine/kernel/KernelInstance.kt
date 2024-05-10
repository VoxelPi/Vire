package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.FieldStateMap
import net.voxelpi.vire.engine.kernel.variable.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.OutputStateMap
import net.voxelpi.vire.engine.kernel.variable.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.SettingStateMap
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.VectorVariableSizeProvider

public interface KernelInstance :
    ParameterStateProvider,
    VectorVariableSizeProvider,
    SettingStateProvider,
    FieldStateProvider,
    OutputStateProvider {

    public val kernelVariant: KernelVariant

    public val kernel: Kernel
        get() = kernelVariant.kernel

    override fun <T> get(parameter: Parameter<T>): T = kernelVariant[parameter]

    override fun size(vector: VectorVariable<*>): Int = kernelVariant.size(vector)

    override fun size(vectorName: String): Int = kernelVariant.size(vectorName)
}

internal class KernelInstanceImpl(
    override val kernelVariant: KernelVariantImpl,
    override val variableStates: Map<String, Any?>,
) : KernelInstance, SettingStateMap, FieldStateMap, OutputStateMap {

    override val kernel: KernelImpl
        get() = kernelVariant.kernel
}
