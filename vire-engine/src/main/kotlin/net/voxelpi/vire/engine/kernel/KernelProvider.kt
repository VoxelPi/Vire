package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateMap

/**
 * Provides a kernel.
 */
public interface KernelProvider : Kernel {

    /**
     * The provided kernel.
     */
    public val kernel: Kernel

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
