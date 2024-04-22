package net.voxelpi.vire.engine.circuit.kernel.compiled

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.kernel.Kernel
import net.voxelpi.vire.engine.circuit.kernel.KernelConfiguration
import net.voxelpi.vire.engine.circuit.kernel.KernelConfigurationException
import net.voxelpi.vire.engine.circuit.kernel.KernelConfigurationImpl
import net.voxelpi.vire.engine.circuit.kernel.KernelConfigurationResults
import net.voxelpi.vire.engine.circuit.kernel.KernelImpl
import net.voxelpi.vire.engine.circuit.kernel.KernelState
import net.voxelpi.vire.engine.circuit.kernel.variable.Variable

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

    override fun configureKernel(configuration: KernelConfiguration): Result<KernelConfigurationResults> {
        require(configuration is KernelConfigurationImpl)
        val context = ConfigurationContextImpl(configuration)
        try {
            configure(context)
        } catch (exception: KernelConfigurationException) {
            return Result.failure(exception)
        }
        return Result.success(KernelConfigurationResults(configuration, context.ioVectorSizes))
    }

    override fun initializeKernel(state: KernelState) {
        TODO("Not yet implemented")
    }

    override fun updateKernel(state: KernelState) {
        TODO("Not yet implemented")
    }
}
