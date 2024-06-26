package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.provider.MutableParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.MutableParameterStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableParameterStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.storage.mutableParameterStateStorage

/**
 * A builder for a kernel variant.
 */
public interface KernelVariantBuilder : MutableParameterStateProvider {

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
    override val parameterStateStorage: MutableParameterStateStorage,
) : KernelVariantBuilder, MutableParameterStateStorageWrapper {

    constructor(kernel: KernelImpl, parameterStateProvider: ParameterStateProvider) :
        this(kernel, mutableParameterStateStorage(kernel, parameterStateProvider))

    override fun get(parameterName: String): Any? {
        // Check that a parameter with the given name exists.
        val parameter = kernel.parameter(parameterName)
            ?: throw IllegalArgumentException("Unknown parameter '$parameterName'")

        // Return the value of the parameter.
        return parameterStateStorage[parameter]
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(parameterName: String, value: Any?) {
        // Check that a parameter with the given name exists.
        val parameter = kernel.parameter(parameterName) as Parameter<Any?>?
            ?: throw IllegalArgumentException("Unknown parameter '$parameterName'")

        // Update the value of the parameter.
        parameterStateStorage[parameter] = value
    }

    fun update(values: Map<String, Any?>): KernelVariantBuilderImpl {
        parameterStateStorage.update(values)
        return this
    }

    fun build(): KernelVariantConfig {
        return KernelVariantConfig(kernel, parameterStateStorage.copy())
    }
}
