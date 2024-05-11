package net.voxelpi.vire.engine.kernel.script

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelInitializationException
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.KernelVariantWrapper
import net.voxelpi.vire.engine.kernel.variable.MutableFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.MutableFieldStateStorage
import net.voxelpi.vire.engine.kernel.variable.MutableFieldStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.MutableOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.MutableOutputStateStorage
import net.voxelpi.vire.engine.kernel.variable.MutableOutputStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.mutableFieldStateStorage
import net.voxelpi.vire.engine.kernel.variable.mutableOutputStateStorage

public interface InitializationContext :
    ParameterStateProvider,
    VectorSizeProvider,
    SettingStateProvider,
    MutableFieldStateProvider,
    MutableOutputStateProvider {

    /**
     * The kernel which of which the instance was created.
     */
    public val kernel: Kernel

    /**
     * The kernel variant of which the instance was created.
     */
    public val kernelVariant: KernelVariant

    /**
     * Stops the initialization of the kernel instance.
     * This is intended to allow the kernel to signal an invalid setting state.
     */
    public fun signalInvalidConfiguration(message: String = "Invalid kernel initialization"): Nothing {
        throw KernelInitializationException(message)
    }
}

internal class InitializationContextImpl(
    override val kernelVariant: KernelVariantImpl,
    private val settingStateProvider: SettingStateProvider,
) : InitializationContext, KernelVariantWrapper, MutableFieldStateStorageWrapper, MutableOutputStateStorageWrapper {

    override val kernel: Kernel
        get() = kernelVariant.kernel

    val variables: MutableMap<String, Variable<*>> = kernel.variables()
        .associateBy { it.name }
        .toMutableMap()

    override val fieldStateStorage: MutableFieldStateStorage = mutableFieldStateStorage(
        kernelVariant,
        kernelVariant.fields().associate { it.name to it.initialization() },
    )

    override val outputStateStorage: MutableOutputStateStorage = mutableOutputStateStorage(
        kernelVariant,
        kernelVariant.outputs().associate {
            it.name to Array(if (it is OutputVector) kernelVariant.size(it) else 1) { LogicState.EMPTY }
        },
    )
    override val variableProvider: VariableProvider
        get() = kernelVariant

    override fun <T> get(setting: Setting<T>): T {
        return settingStateProvider[setting]
    }
}
