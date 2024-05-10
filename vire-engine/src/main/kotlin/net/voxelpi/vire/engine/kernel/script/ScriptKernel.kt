package net.voxelpi.vire.engine.kernel.script

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelConfigurationException
import net.voxelpi.vire.engine.kernel.KernelImpl
import net.voxelpi.vire.engine.kernel.KernelVariantConfig
import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.MutableKernelState
import net.voxelpi.vire.engine.kernel.variable.Variable

public interface ScriptKernel : Kernel {

    /**
     * The configuration action of the kernel.
     */
    public val configure: (ConfigurationContext) -> Unit

    /**
     * The initialization action of the kernel.
     */
    public val initialize: (InitializationContext) -> Unit

    /**
     * The update action fo the kernel.
     */
    public val update: (UpdateContext) -> Unit
}

internal class ScriptKernelImpl(
    id: Identifier,
    tags: Set<Identifier>,
    properties: Map<Identifier, String>,
    override val variables: Map<String, Variable<*>>,
    override val configure: (ConfigurationContext) -> Unit,
    override val initialize: (InitializationContext) -> Unit,
    override val update: (UpdateContext) -> Unit,
) : KernelImpl(id, tags, properties), ScriptKernel {

    override fun generateVariant(config: KernelVariantConfig): Result<KernelVariantImpl> {
        val context = ConfigurationContextImpl(this, config)
        try {
            configure(context)
        } catch (exception: KernelConfigurationException) {
            return Result.failure(exception)
        }

        val variant = KernelVariantImpl(this, context.variables, config.variableStates, context.vectorVariableSizes)
        return Result.success(variant)
    }

    override fun updateKernel(state: MutableKernelState) {
        val context = UpdateContextImpl(state)
        update(context)
    }
}
