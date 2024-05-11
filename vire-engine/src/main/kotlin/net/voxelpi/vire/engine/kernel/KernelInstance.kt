package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.FieldStateStorage
import net.voxelpi.vire.engine.kernel.variable.FieldStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.OutputStateStorage
import net.voxelpi.vire.engine.kernel.variable.OutputStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.SettingStateStorage
import net.voxelpi.vire.engine.kernel.variable.SettingStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable

public interface KernelInstance :
    ParameterStateProvider,
    VectorSizeProvider,
    SettingStateProvider,
    FieldStateProvider,
    OutputStateProvider {

    public val kernelVariant: KernelVariant

    public val kernel: Kernel
        get() = kernelVariant.kernel

    override val variableProvider: VariableProvider
        get() = kernelVariant

    override fun <T> get(parameter: Parameter<T>): T = kernelVariant[parameter]

    override fun size(vector: VectorVariable<*>): Int = kernelVariant.size(vector)

    override fun size(vectorName: String): Int = kernelVariant.size(vectorName)
}

internal class KernelInstanceImpl(
    override val kernelVariant: KernelVariantImpl,
    override val settingStateStorage: SettingStateStorage,
    override val fieldStateStorage: FieldStateStorage,
    override val outputStateStorage: OutputStateStorage,
) : KernelInstance, SettingStateStorageWrapper, FieldStateStorageWrapper, OutputStateStorageWrapper {

    override val kernel: KernelImpl
        get() = kernelVariant.kernel

    override val variableProvider: VariableProvider
        get() = kernelVariant
}
