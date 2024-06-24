package net.voxelpi.vire.engine.kernel.registered

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.environment.library.Library
import net.voxelpi.vire.engine.kernel.KernelImpl
import net.voxelpi.vire.engine.kernel.builder.ConfigurationContext
import net.voxelpi.vire.engine.kernel.builder.InitializationContext
import net.voxelpi.vire.engine.kernel.builder.UpdateContext
import net.voxelpi.vire.engine.kernel.variable.Variable

public interface LibraryKernel : RegisteredKernel {

    public val library: Library
}

internal class LibraryKernelImpl(
    id: Identifier,
    override val library: Library,
    tags: Set<Identifier>,
    properties: Map<Identifier, String>,
    variables: Map<String, Variable<*>>,
    configurationAction: (ConfigurationContext) -> Unit,
    initializationAction: (InitializationContext) -> Unit,
    updateAction: (UpdateContext) -> Unit,
) : RegisteredKernelImpl(id, tags, properties, variables, configurationAction, initializationAction, updateAction), LibraryKernel {

    constructor(id: Identifier, library: Library, kernel: KernelImpl) : this(
        id,
        library,
        kernel.tags.toSet(),
        kernel.properties.toMap(),
        kernel.variables.toMap(),
        kernel.configurationAction,
        kernel.initializationAction,
        kernel.updateAction,
    )

    constructor(name: String, library: Library, kernel: KernelImpl) : this(
        Identifier(library.id, name),
        library,
        kernel.tags.toSet(),
        kernel.properties.toMap(),
        kernel.variables.toMap(),
        kernel.configurationAction,
        kernel.initializationAction,
        kernel.updateAction,
    )
}
