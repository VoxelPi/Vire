package net.voxelpi.vire.engine.kernel.library

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.environment.library.Library
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.KernelVariantBuilder
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateMap

/**
 * @property id the id of the kernel.
 */
public data class LibraryKernel internal constructor(
    val id: Identifier,
    val library: Library,
    val kernel: Kernel,
) : Kernel {

    override val tags: Set<Identifier>
        get() = kernel.tags

    override val properties: Map<Identifier, String>
        get() = kernel.properties

    override fun createVariant(base: ParameterStateProvider): Result<KernelVariant> {
        return kernel.createVariant(base)
    }

    override fun createVariant(base: ParameterStateProvider, lambda: KernelVariantBuilder.() -> Unit): Result<KernelVariant> {
        return kernel.createVariant(base, lambda)
    }

    override fun createVariant(values: ParameterStateMap, base: ParameterStateProvider): Result<KernelVariant> {
        return kernel.createVariant(values, base)
    }

    override fun generateDefaultParameterStates(): ParameterStateProvider {
        return kernel.generateDefaultParameterStates()
    }

    override fun variables(): Collection<Variable<*>> {
        return kernel.variables()
    }

    override fun variable(name: String): Variable<*>? {
        return kernel.variable(name)
    }
}
