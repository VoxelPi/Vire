package net.voxelpi.vire.engine.kernel.registered

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelImpl
import net.voxelpi.vire.engine.kernel.builder.ConfigurationContext
import net.voxelpi.vire.engine.kernel.builder.InitializationContext
import net.voxelpi.vire.engine.kernel.builder.KernelBuilder
import net.voxelpi.vire.engine.kernel.builder.UpdateContext
import net.voxelpi.vire.engine.kernel.variable.Variable

/**
 * A kernel that is registered under a given id.
 */
public interface RegisteredKernel : Kernel {

    /**
     * The id of under which the kernel is registered.
     */
    public val id: Identifier
}

/**
 * Creates a new [RegisteredKernel] using the given [lambda].
 */
internal fun registeredKernel(id: Identifier, lambda: KernelBuilder.() -> Unit): RegisteredKernel {
    val builder = RegisteredKernelBuilderImpl(id)
    builder.lambda()
    return builder.build()
}

internal open class RegisteredKernelImpl(
    override val id: Identifier,
    tags: Set<Identifier>,
    properties: Map<Identifier, String>,
    variables: Map<String, Variable<*>>,
    configurationAction: (ConfigurationContext) -> Unit,
    initializationAction: (InitializationContext) -> Unit,
    updateAction: (UpdateContext) -> Unit,
) : KernelImpl(tags, properties, variables, configurationAction, initializationAction, updateAction), RegisteredKernel {

    constructor(id: Identifier, kernel: KernelImpl) : this(
        id,
        kernel.tags.toSet(),
        kernel.properties.toMap(),
        kernel.variables.toMap(),
        kernel.configurationAction,
        kernel.initializationAction,
        kernel.updateAction,
    )
}
