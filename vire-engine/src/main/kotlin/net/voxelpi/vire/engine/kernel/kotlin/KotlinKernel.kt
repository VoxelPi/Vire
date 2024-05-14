package net.voxelpi.vire.engine.kernel.kotlin

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelConfigurationException
import net.voxelpi.vire.engine.kernel.KernelImpl
import net.voxelpi.vire.engine.kernel.KernelInitializationException
import net.voxelpi.vire.engine.kernel.KernelInstanceConfig
import net.voxelpi.vire.engine.kernel.KernelInstanceImpl
import net.voxelpi.vire.engine.kernel.KernelVariantConfig
import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.MutableKernelState
import net.voxelpi.vire.engine.kernel.variable.Variable

public interface KotlinKernel : Kernel {

    /**
     * The configuration action of the kernel.
     */
    public val configurationAction: (ConfigurationContext) -> Unit

    /**
     * The initialization action of the kernel.
     */
    public val initializationAction: (InitializationContext) -> Unit

    /**
     * The update action fo the kernel.
     */
    public val updateAction: (UpdateContext) -> Unit
}

internal class KotlinKernelImpl(
    id: Identifier,
    tags: Set<Identifier>,
    properties: Map<Identifier, String>,
    override val variables: Map<String, Variable<*>>,
    override val configurationAction: (ConfigurationContext) -> Unit,
    override val initializationAction: (InitializationContext) -> Unit,
    override val updateAction: (UpdateContext) -> Unit,
) : KernelImpl(id, tags, properties), KotlinKernel {

    override fun generateVariant(config: KernelVariantConfig): Result<KernelVariantImpl> {
        val context = ConfigurationContextImpl(this, config)
        try {
            configurationAction(context)
        } catch (exception: KernelConfigurationException) {
            return Result.failure(exception)
        }

        val variant = KernelVariantImpl(
            this,
            context.variables,
            config.parameterStateStorage,
            context.vectorSizeStorage.copy(),
        )
        return Result.success(variant)
    }

    override fun generateInstance(config: KernelInstanceConfig): Result<KernelInstanceImpl> {
        val context = InitializationContextImpl(config.kernelVariant, config)
        try {
            initializationAction(context)
        } catch (exception: KernelInitializationException) {
            return Result.failure(exception)
        }

        val instance = KernelInstanceImpl(
            config.kernelVariant,
            config.settingStateStorage,
            context.fieldStateStorage.copy(),
            context.outputStateStorage.copy(),
        )
        return Result.success(instance)
    }

    override fun updateKernel(state: MutableKernelState) {
        val context = UpdateContextImpl(state)
        updateAction(context)
    }
}
