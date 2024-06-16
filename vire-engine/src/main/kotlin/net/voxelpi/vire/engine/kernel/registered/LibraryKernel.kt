package net.voxelpi.vire.engine.kernel.registered

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.environment.library.Library
import net.voxelpi.vire.engine.kernel.Kernel

public data class LibraryKernel internal constructor(
    override val id: Identifier,
    val library: Library,
    override val kernel: Kernel,
) : RegisteredKernel {

    internal constructor(name: String, library: Library, kernel: Kernel) : this(Identifier(library.id, name), library, kernel)
}
