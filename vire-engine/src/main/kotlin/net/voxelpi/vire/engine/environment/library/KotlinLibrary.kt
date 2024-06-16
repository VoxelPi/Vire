package net.voxelpi.vire.engine.environment.library

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.registered.LibraryKernel

/**
 * A library for a vire environment that was defined in kotlin source code.
 */
public abstract class KotlinLibrary(
    override val id: String,
    override val name: String = id,
    override val description: String = "",
    override val dependencies: List<String> = emptyList(),
) : Library {

    private val kernels: MutableMap<Identifier, LibraryKernel> = mutableMapOf()

    override fun kernels(): Collection<LibraryKernel> {
        return kernels.values
    }

    override fun kernel(id: Identifier): LibraryKernel? {
        return kernels[id]
    }

    protected fun register(name: String, kernel: Kernel): LibraryKernel {
        return register(Identifier(this.id, name), kernel)
    }

    protected fun register(id: Identifier, kernel: Kernel): LibraryKernel {
        require(id !in kernels)
        val registration = LibraryKernel(id, this, kernel)

        kernels[id] = registration
        return registration
    }
}
