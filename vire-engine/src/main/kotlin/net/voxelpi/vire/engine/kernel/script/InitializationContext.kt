package net.voxelpi.vire.engine.kernel.script

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelInitializationException
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.KernelVariantWrapper
import net.voxelpi.vire.engine.kernel.variable.MutableFieldStateMap
import net.voxelpi.vire.engine.kernel.variable.MutableFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.MutableOutputStateMap
import net.voxelpi.vire.engine.kernel.variable.MutableOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VectorVariableSizeProvider

public interface InitializationContext :
    ParameterStateProvider,
    VectorVariableSizeProvider,
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
) : InitializationContext, KernelVariantWrapper, MutableFieldStateMap, MutableOutputStateMap {

    override val kernel: Kernel
        get() = kernelVariant.kernel

    val variables: MutableMap<String, Variable<*>> = kernel.variables()
        .associateBy { it.name }
        .toMutableMap()

    override val variableStates: MutableMap<String, Any?>

    init {
        val variableStates = mutableMapOf<String, Any?>()
        variableStates += kernel.fields().associate { it.name to it.initialization() }
        variableStates += kernel.outputs().associate { it.name to LogicState.EMPTY }
        this.variableStates = variableStates
    }

    override fun <T> get(setting: Setting<T>): T {
        return settingStateProvider[setting]
    }
}
