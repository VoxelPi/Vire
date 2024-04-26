package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.IOVector
import net.voxelpi.vire.engine.kernel.variable.IOVectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.Input
import net.voxelpi.vire.engine.kernel.variable.Output
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider

/**
 * An instance of a kernel.
 */
public interface KernelVariant : ParameterStateProvider, IOVectorSizeProvider {

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
     * Returns the size of the given IO-vector with the given [variableName].
     */
    public fun size(variableName: String): Int

    /**
     * Updates the state of the parameters of the instance using the given [lambda].
     */
    public fun modify(lambda: KernelVariantBuilder.() -> Unit): Result<Unit>

    /**
     * Updates the state of the parameters of the instance to the given [values].
     */
    public fun modify(values: Map<String, Any?>): Result<Unit>
}

internal class KernelVariantImpl(
    override val kernel: KernelImpl,
    builder: KernelVariantBuilderImpl,
) : KernelVariant {

    private var parameterStates: MutableMap<String, Any?> = mutableMapOf()

    private var ioVectorSizes: MutableMap<String, Int> = mutableMapOf()

    init {
        modify(builder).getOrThrow()
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
        ioVectorSizes = variantData.ioVectorSizes.toMutableMap()
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

    override fun size(input: Input): Int {
        // Check that the io vector is defined on the kernel.
        require(kernel.hasInput(input.name))

        // Return the size.
        return ioVectorSizes[input.name]!!
    }

    override fun size(output: Output): Int {
        // Check that the io vector is defined on the kernel.
        require(kernel.hasOutput(output.name))

        // Return the size.
        return ioVectorSizes[output.name]!!
    }

    override fun size(variableName: String): Int {
        // Check that the io vector is defined on the kernel.
        require(kernel.hasInput(variableName) || kernel.hasOutput(variableName))

        // Return the size.
        return ioVectorSizes[variableName]!!
    }

    /**
     * Changes the size of the given [ioVector] to the given [size].
     */
    fun resize(ioVector: IOVector, size: Int) {
        // Check that the io vector is defined on the kernel.
        require(kernel.hasInput(ioVector.name) || kernel.hasOutput(ioVector.name))

        // Modify the size of the io vector.
        ioVectorSizes[ioVector.name] = size
    }

    /**
     * Changes the size of the given IO-vector with the given [variableName] to the given [size].
     */
    fun resize(variableName: String, size: Int) {
        // Check that the io vector is defined on the kernel.
        require(kernel.hasInput(variableName) || kernel.hasOutput(variableName))

        // Modify the size of the io vector.
        ioVectorSizes[variableName] = size
    }
}
