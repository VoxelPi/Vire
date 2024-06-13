package net.voxelpi.vire.engine.environment.library

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.library.LibraryKernel

/**
 * A library for a vire environment.
 */
public interface Library {

    public val id: String

    public val name: String

    public val description: String

    public val dependencies: List<String>

    public fun kernels(): Collection<LibraryKernel>

    public fun kernel(id: Identifier): LibraryKernel?
}
