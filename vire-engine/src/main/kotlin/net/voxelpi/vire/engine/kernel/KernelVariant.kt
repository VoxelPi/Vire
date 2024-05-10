package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.ParameterStateMap
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariableSizeMap
import net.voxelpi.vire.engine.kernel.variable.VectorVariableSizeProvider

/**
 * An instance of a kernel.
 */
public interface KernelVariant : ParameterStateProvider, VectorVariableSizeProvider {

    /**
     * The kernel which of which the instance was created.
     */
    public val kernel: Kernel

    /**
     * Returns the current value of the parameter with the given [parameterName].
     *
     * @param parameterName the name of the parameter of which the value should be returned.
     */
    public operator fun get(parameterName: String): Any?

    /**
     * Creates a new copy of this kernel variant.
     */
    public fun copy(): Result<KernelVariant>

    /**
     * Creates a new copy of this kernel variant, whose parameters have been modified using the given [lambda].
     */
    public fun copy(lambda: KernelVariantBuilder.() -> Unit): Result<KernelVariant>

    /**
     * Creates a new copy of this kernel variant, whose parameters have been modified using the given [values].
     */
    public fun copy(values: Map<String, Any?>): Result<KernelVariant>
}

internal class KernelVariantImpl(
    override val kernel: KernelImpl,
    override val variableStates: Map<String, Any?>,
    override var vectorVariableSizes: Map<String, Int>,
) : KernelVariant, VectorVariableSizeMap, ParameterStateMap {

    override fun copy(): Result<KernelVariantImpl> {
        return kernel.createVariant(this)
    }

    override fun copy(lambda: KernelVariantBuilder.() -> Unit): Result<KernelVariantImpl> {
        return kernel.createVariant(this, lambda)
    }

    override fun copy(values: Map<String, Any?>): Result<KernelVariantImpl> {
        return kernel.createVariant(values, this)
    }

    override fun get(parameterName: String): Any? {
        // Check that a parameter with the given name exists.
        require(kernel.hasParameter(parameterName)) { "Unknown parameter $parameterName" }

        // Return the value of the parameter.
        return variableStates[parameterName]
    }
}
