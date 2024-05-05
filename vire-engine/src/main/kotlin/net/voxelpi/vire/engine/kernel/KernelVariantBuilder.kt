package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.MutableParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.Parameter

/**
 * An instance of a kernel.
 */
public interface KernelVariantBuilder : MutableParameterStateProvider {

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
     * Sets the value of the parameter with the given [parameterName] to the given [value].
     *
     * @param parameterName the name of the parameter of which the value should be modified.
     * @param value the new value of the parameter.
     */
    public operator fun set(parameterName: String, value: Any?)
}

internal class KernelVariantBuilderImpl(
    override val kernel: KernelImpl,
    val parameterStates: MutableMap<String, Any?> = kernel.generateDefaultParameterStates(),
) : KernelVariantBuilder {

    init {
        for (parameterName in parameterStates.keys) {
            // Check that only existing parameters are specified.
            require(kernel.hasParameter(parameterName)) { "Specified value for unknown parameter '$parameterName'" }
        }
        for (parameter in kernel.parameters()) {
            // Check that every parameter has an assigned value.
            require(parameter.name in parameterStates) { "No value for the parameter ${parameter.name}" }
            // Check that the assigned value is valid for the given parameter.
            require(parameter.isValidTypeAndValue(parameterStates[parameter.name])) { "Invalid value for the parameter ${parameter.name}" }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameter: Parameter<T>): T {
        // Check that a parameter with the given name exists.
        require(kernel.hasParameter(parameter.name)) { "Unknown parameter ${parameter.name}" }

        // Return the value of the parameter.
        return parameterStates[parameter.name] as T
    }

    override fun <T> set(parameter: Parameter<T>, value: T) {
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

    override fun set(parameterName: String, value: Any?) {
        // Check that a parameter with the given name exists.
        val parameter = kernel.parameter(parameterName)
            ?: throw IllegalArgumentException("Unknown parameter '$parameterName'")

        // Check that the value is valid for the specified parameter.
        require(parameter.isValidTypeAndValue(value)) { "Value $value does not meet the requirements for the parameter ${parameter.name}" }

        // Update the value of the parameter.
        parameterStates[parameter.name] = value
    }
}

internal data class KernelVariantData(
    val kernelVariantBuilder: KernelVariantBuilder,
    val ioVectorSizes: Map<String, Int>,
)
