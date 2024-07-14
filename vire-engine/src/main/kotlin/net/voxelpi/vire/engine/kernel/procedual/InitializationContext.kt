package net.voxelpi.vire.engine.kernel.procedual

import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelInitializationException
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.KernelVariantWrapper
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialFieldStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialOutputStateProviderWrapper
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
    MutablePartialFieldStateProvider,
    MutablePartialOutputStateProvider {

    /**
     * The kernel which of which the instance was created.
     */
    public val kernel: ProceduralKernel

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
    override val kernel: ProceduralKernel,
    override val kernelVariant: KernelVariantImpl,
    override val settingStateProvider: SettingStateProvider,
    override val fieldStateProvider: MutablePartialFieldStateProvider,
    override val outputStateProvider: MutablePartialOutputStateProvider,
) : InitializationContext,
    KernelVariantWrapper,
    SettingStateProviderWrapper,
    MutablePartialFieldStateProviderWrapper,
    MutablePartialOutputStateProviderWrapper {

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
