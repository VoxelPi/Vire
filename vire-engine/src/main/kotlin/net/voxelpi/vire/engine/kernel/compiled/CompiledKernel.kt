package net.voxelpi.vire.engine.kernel.compiled

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelConfigurationException
import net.voxelpi.vire.engine.kernel.KernelImpl
import net.voxelpi.vire.engine.kernel.KernelVariantBuilder
import net.voxelpi.vire.engine.kernel.KernelVariantBuilderImpl
import net.voxelpi.vire.engine.kernel.KernelVariantData
import net.voxelpi.vire.engine.kernel.variable.Variable

public interface CompiledKernel : Kernel {

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

internal class CompiledKernelImpl(
    id: Identifier,
    tags: Set<Identifier>,
    properties: Map<Identifier, String>,
    override val variables: Map<String, Variable<*>>,
    override val configure: (ConfigurationContext) -> Unit,
    override val initialize: (InitializationContext) -> Unit,
    override val update: (UpdateContext) -> Unit,
) : KernelImpl(id, tags, properties), CompiledKernel {

    override fun generateVariantData(builder: KernelVariantBuilder): Result<KernelVariantData> {
        require(builder is KernelVariantBuilderImpl)
        val context = ConfigurationContextImpl(this, builder)
        try {
            configure(context)
        } catch (exception: KernelConfigurationException) {
            return Result.failure(exception)
        }
        return Result.success(KernelVariantData(builder, context.vectorVariableSizes))
    }
}
