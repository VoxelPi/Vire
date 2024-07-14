package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.patch.MutableParameterStatePatch
import net.voxelpi.vire.engine.kernel.variable.patch.ParameterStatePatch
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialParameterStateProvider

public interface SpecializedKernel : WrappedKernel {

    public val specifiedParameters: PartialParameterStateProvider
}

internal open class SpecializedKernelImpl(
    final override val kernel: Kernel,
    specifiedParameters: PartialParameterStateProvider,
    additionalTags: Set<Identifier>,
    additionalProperties: Map<Identifier, String>
) : SpecializedKernel {

    override val specifiedParameters: ParameterStatePatch = ParameterStatePatch(kernel, specifiedParameters)

    override val tags: Set<Identifier> = kernel.tags + additionalTags

    override val properties: Map<Identifier, String> = kernel.properties + additionalProperties

    override fun createVariant(base: PartialParameterStateProvider, lambda: KernelVariantBuilder.() -> Unit): Result<KernelVariant> {
        val states = MutableParameterStatePatch(kernel, base)
        states.applyParameterStatePatch(specifiedParameters)

        return kernel.createVariant(base, lambda)
    }

    override fun createSpecialization(
        additionalTags: Set<Identifier>,
        additionalProperties: Map<Identifier, String>,
        lambda: MutablePartialParameterStateProvider.() -> Unit,
    ): SpecializedKernel {
        val patch = MutableParameterStatePatch(this, emptyMap())
        patch.lambda()
        return SpecializedKernelImpl(this, patch, additionalTags, additionalProperties)
    }

    override fun generateDefaultParameterStates(): PartialParameterStateProvider {
        val defaultStates = kernel.generateDefaultParameterStates()

        val states = mutableMapOf<String, Any?>()
        for (parameter in kernel.parameters()) {
            if (!specifiedParameters.hasValue(parameter)) {
                states[parameter.name] = defaultStates[parameter]
            }
        }

        return ParameterStatePatch(this, states)
    }

    override fun variables(): Collection<Variable<*>> {
        return kernel.variables().filter {
            if (it is Parameter<*>) {
                !specifiedParameters.hasValue(it)
            } else {
                true
            }
        }
    }

    override fun variable(name: String): Variable<*>? {
        val variable = kernel.variable(name)
        if (variable is Parameter<*> && specifiedParameters.hasValue(variable)) {
            return null
        }
        return kernel.variable(name)
    }
}
