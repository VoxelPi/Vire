package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.storage.FieldStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.FieldStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.storage.OutputStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.OutputStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.storage.mutableInputStateStorage

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

    public fun initialKernelState(): MutableKernelState
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

    override fun initialKernelState(): MutableKernelStateImpl {
        val inputStateStorage = mutableInputStateStorage(
            kernelVariant,
            kernelVariant.inputs().associate { input ->
                val size = when (input) {
                    is InputScalar -> 1
                    is InputVector -> kernelVariant.size(input)
                    is InputVectorElement -> throw IllegalStateException("Vector elements are not allowed")
                }
                input.name to Array(size) { LogicState.EMPTY }
            },
        )

        return MutableKernelStateImpl(
            this,
            fieldStateStorage.mutableCopy(),
            inputStateStorage,
            outputStateStorage.mutableCopy(),
        )
    }
}
