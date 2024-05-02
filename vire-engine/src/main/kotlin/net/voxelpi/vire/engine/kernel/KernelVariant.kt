package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.MutableVectorVariableSizeMap
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
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
     * Updates the state of the parameters of the instance using the given [lambda].
     */
    public fun modify(lambda: KernelVariantBuilder.() -> Unit): Result<Unit>

    /**
     * Updates the state of the parameters of the instance to the given [values].
     */
    public fun modify(values: Map<String, Any?>): Result<Unit>
}

internal class KernelVariantImpl private constructor(
    override val kernel: KernelImpl,
) : KernelVariant, MutableVectorVariableSizeMap {

    private var parameterStates: MutableMap<String, Any?> = mutableMapOf()

    override var vectorVariableSizes: MutableMap<String, Int> = mutableMapOf()

    constructor(kernel: KernelImpl, builder: KernelVariantBuilderImpl) : this(kernel) {
        modify(builder).getOrThrow()
    }

    fun clone(): KernelVariantImpl {
        val clone = KernelVariantImpl(kernel)
        clone.parameterStates = parameterStates.toMutableMap()
        clone.vectorVariableSizes = vectorVariableSizes.toMutableMap()
        return clone
    }

    override fun modify(lambda: KernelVariantBuilder.() -> Unit): Result<Unit> {
        // Create a new builder from the current state and apply the lambda on it.
        val builder = KernelVariantBuilderImpl(kernel, parameterStates.toMutableMap())
            .apply(lambda)

        // Apply the builder.
        return modify(builder)
    }

    override fun modify(values: Map<String, Any?>): Result<Unit> {
        // Create a new builder from the current state and set the provided values.
        val builder = KernelVariantBuilderImpl(kernel, parameterStates.toMutableMap())
        for ((parameterName, parameterValue) in values) {
            // Check that only existing parameters are specified.
            val parameter = kernel.parameter(parameterName)
                ?: throw IllegalArgumentException("Unknown parameter '$parameterName'")

            // Check that the value is valid for the parameter.
            require(parameter.isValidValue(parameterValue)) { "Invalid value for the parameter ${parameter.name}" }
            this[parameterName] = parameterValue
        }

        // Apply the builder.
        return modify(builder)
    }

    private fun modify(builder: KernelVariantBuilderImpl): Result<Unit> {
        // Let the kernel process the builder.
        val variantData = kernel.generateVariantData(builder).getOrElse {
            return Result.failure(it)
        }

        // Update the variant data.
        parameterStates = builder.parameterStates
        vectorVariableSizes = variantData.ioVectorSizes.toMutableMap()
        return Result.success(Unit)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameter: Parameter<T>): T {
        // Check that a parameter with the given name exists.
        require(kernel.hasParameter(parameter.name)) { "Unknown parameter ${parameter.name}" }

        // Return the value of the parameter.
        return parameterStates[parameter.name] as T
    }

    operator fun <T> set(parameter: Parameter<T>, value: T) {
        // Check that a parameter with the given name exists.
        require(kernel.hasParameter(parameter.name)) { "Unknown parameter ${parameter.name}" }

        // Check that the value is valid for the specified parameter.
        require(parameter.isValidValue(value)) { "Value $value does not meet the requirements for the parameter ${parameter.name}" }

        // Update the value of the parameter.
        parameterStates[parameter.name] = value
    }

    override fun get(parameterName: String): Any? {
        // Check that a parameter with the given name exists.
        require(kernel.hasParameter(parameterName)) { "Unknown parameter $parameterName" }

        // Return the value of the parameter.
        return parameterStates[parameterName]
    }

    operator fun set(parameterName: String, value: Any?) {
        // Check that a parameter with the given name exists.
        val parameter = kernel.parameter(parameterName)
            ?: throw IllegalArgumentException("Unknown parameter '$parameterName'")

        // Check that the value is valid for the specified parameter.
        require(parameter.isValidValue(value)) { "Value $value does not meet the requirements for the parameter ${parameter.name}" }

        // Update the value of the parameter.
        parameterStates[parameter.name] = value
    }
}
