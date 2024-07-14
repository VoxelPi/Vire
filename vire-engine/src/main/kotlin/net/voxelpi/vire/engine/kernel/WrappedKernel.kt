package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.provider.PartialParameterStateProvider

public interface WrappedKernel : Kernel {

    public val kernel: Kernel

    override val tags: Set<Identifier>
        get() = kernel.tags

    override val properties: Map<Identifier, String>
        get() = kernel.properties

    override fun createVariant(base: PartialParameterStateProvider, lambda: KernelVariantBuilder.() -> Unit): Result<KernelVariant> {
        return kernel.createVariant(base, lambda)
    }

    override fun generateDefaultParameterStates(): PartialParameterStateProvider {
        return kernel.generateDefaultParameterStates()
    }

    override fun variables(): Collection<Variable<*>> {
        return kernel.variables()
    }

    override fun variable(name: String): Variable<*>? {
        return kernel.variable(name)
    }
}
