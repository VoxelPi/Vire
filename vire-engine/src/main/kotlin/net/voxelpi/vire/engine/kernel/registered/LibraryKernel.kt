package net.voxelpi.vire.engine.kernel.registered

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.environment.library.Library
import net.voxelpi.vire.engine.kernel.Kernel

public interface LibraryKernel : RegisteredKernel {

    public val library: Library
}

internal class LibraryKernelImpl(
    override val library: Library,
    id: Identifier,
    kernel: Kernel,
) : RegisteredKernelImpl(id, kernel), LibraryKernel {

    constructor(library: Library, name: String, kernel: Kernel) : this(library, Identifier(library.id, name), kernel)
}
