package net.voxelpi.vire.engine.kernel.builder

import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelInitializationException
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.KernelVariantWrapper
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableFieldStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.MutableOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableOutputStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.storage.FieldStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.FieldStateStorageWrapper

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
     * Checks whether the given [field] has been initialized.
     */
    public fun isInitialized(field: Field<*>): Boolean

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
    override val settingStateProvider: SettingStateProvider,
    override val fieldStateProvider: MutableFieldStateProvider,
    override val outputStateProvider: MutableOutputStateProvider,
) : InitializationContext,
    KernelVariantWrapper,
    SettingStateProviderWrapper,
    MutableFieldStateProviderWrapper,
    MutableOutputStateProviderWrapper {

    override val kernel: Kernel
        get() = kernelVariant.kernel

    override val variableProvider: VariableProvider
        get() = kernelVariant

    val variables: MutableMap<String, Variable<*>> = kernel.variables()
        .associateBy { it.name }
        .toMutableMap()

    override fun isInitialized(field: Field<*>): Boolean {
        return when (fieldStateProvider) {
            is FieldStateStorage -> fieldStateProvider.hasValue(field)
            is FieldStateStorageWrapper -> fieldStateProvider.hasValue(field)
            else -> true
        }
    }
}
