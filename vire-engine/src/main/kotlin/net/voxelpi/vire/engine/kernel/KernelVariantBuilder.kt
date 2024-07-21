package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.patch.MutableParameterStatePatch
import net.voxelpi.vire.engine.kernel.variable.patch.MutableParameterStatePatchWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialParameterStateProvider

/**
 * A builder for a kernel variant.
 */
public interface KernelVariantBuilder : MutablePartialParameterStateProvider {

    /**
     * The kernel which of which the variant should be created.
     */
    public val kernel: Kernel

    /**
     * Returns the current value of the parameter with the given [parameterName].
     *
     * @param parameterName the name of the parameter of which the value should be returned.
     */
    public operator fun get(parameterName: String): Any?

    /**
     * Sets the value of the parameter with the given [parameterName] to the given [value].
     *
     * @param parameterName the name of the parameter of which the value should be modified.
     * @param value the new value of the parameter.
     */
    public operator fun set(parameterName: String, value: Any?)
}

internal class KernelVariantBuilderImpl(
    override val kernel: KernelImpl,
    override val parameterStatePatch: MutableParameterStatePatch,
) : KernelVariantBuilder, MutableParameterStatePatchWrapper {

    constructor(kernel: KernelImpl, partialParameterStateProvider: PartialParameterStateProvider) :
        this(kernel, MutableParameterStatePatch(kernel, partialParameterStateProvider))

    override fun get(parameterName: String): Any? {
        // Check that a parameter with the given name exists.
        val parameter = kernel.parameter(parameterName)
            ?: throw IllegalArgumentException("Unknown parameter '$parameterName'")

        // Return the value of the parameter.
        return parameterStatePatch[parameter]
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(parameterName: String, value: Any?) {
        // Check that a parameter with the given name exists.
        val parameter = kernel.parameter(parameterName) as Parameter<Any?>?
            ?: throw IllegalArgumentException("Unknown parameter '$parameterName'")

        // Update the value of the parameter.
        parameterStatePatch[parameter] = value
    }

    fun update(values: Map<String, Any?>): KernelVariantBuilderImpl {
        parameterStatePatch.applyParameterStatePatch(values)
        return this
    }

    fun build(): KernelVariantConfig {
        return KernelVariantConfig(kernel, parameterStatePatch.createStorage())
    }
}
