package net.voxelpi.vire.engine.environment.library

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.Kernel

/**
 * A library for a vire environment that was defined in kotlin source code.
 */
public abstract class KotlinLibrary(
    override val id: String,
    override val name: String = id,
    override val description: String = "",
    override val dependencies: List<String> = emptyList(),
) : Library {

    private val kernels: MutableMap<Identifier, Kernel> = mutableMapOf()

    override fun kernels(): Collection<Kernel> {
        return kernels.values
    }

    override fun kernel(id: Identifier): Kernel? {
        return kernels[id]
    }

    protected fun register(kernel: Kernel) {
        require(kernel.id !in kernels)
        kernels[kernel.id] = kernel
    }
}
