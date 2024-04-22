package net.voxelpi.vire.engine.circuit.kernel

import net.voxelpi.vire.engine.circuit.kernel.variable.Parameter

/**
 * An instance of a kernel.
 */
public interface KernelConfiguration {

    /**
     * The kernel which of which the instance was created.
     */
    public val kernel: Kernel

    /**
     * Returns the current value of the given [parameter].
     *
     * @param parameter the parameter of which the value should be returned.
     */
    public operator fun <T> get(parameter: Parameter<T>): T

    /**
     * Returns the current value of the parameter with the given [parameterName].
     *
     * @param parameterName the name of the parameter of which the value should be returned.
     */
    public operator fun get(parameterName: String): Any?

    /**
     * Sets the value of the given [parameter] to the given [value].
     *
     * @param parameter the parameter of which the value should be modified.
     * @param value the new value of the parameter.
     */
    public operator fun <T> set(parameter: Parameter<T>, value: T)

    /**
     * Sets the value of the parameter with the given [parameterName] to the given [value].
     *
     * @param parameterName the name of the parameter of which the value should be modified.
     * @param value the new value of the parameter.
     */
    public operator fun set(parameterName: String, value: Any?)
}

internal class KernelConfigurationImpl(
    override val kernel: KernelImpl,
    val parameterStates: MutableMap<String, Any?> = kernel.generateDefaultParameterStates(),
) : KernelConfiguration {

    init {
        for (parameterName in parameterStates.keys) {
            // Check that only existing parameters are specified.
            require(parameterName in kernel.parameters) { "Specified value for unknown parameter '$parameterName'" }
        }
        for (parameter in kernel.parameters()) {
            // Check that every parameter has an assigned value.
            require(parameter.name in parameterStates) { "No value for the parameter ${parameter.name}" }
            // Check that the assigned value is valid for the given parameter.
            require(parameter.isValidValue(parameterStates[parameter.name])) { "Invalid value for the parameter ${parameter.name}" }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameter: Parameter<T>): T {
        // Check that a parameter with the given name exists.
        require(parameter.name in kernel.parameters) { "Unknown parameter ${parameter.name}" }

        // Return the value of the parameter.
        return parameterStates[parameter.name] as T
    }

    override fun <T> set(parameter: Parameter<T>, value: T) {
        // Check that a parameter with the given name exists.
        require(parameter.name in kernel.parameters) { "Unknown parameter ${parameter.name}" }

        // Check that the value is valid for the specified parameter.
        require(parameter.isValidValue(value)) { "Value $value does not meet the requirements for the parameter ${parameter.name}" }

        // Update the value of the parameter.
        parameterStates[parameter.name] = value
    }

    override fun get(parameterName: String): Any? {
        // Check that a parameter with the given name exists.
        require(parameterName in kernel.parameters) { "Unknown parameter $parameterName" }

        // Return the value of the parameter.
        return parameterStates[parameterName]
    }

    override fun set(parameterName: String, value: Any?) {
        // Check that a parameter with the given name exists.
        require(parameterName in kernel.parameters) { "Unknown parameter $parameterName" }
        val parameter = kernel.parameters[parameterName]!!

        // Check that the value is valid for the specified parameter.
        require(parameter.isValidValue(value)) { "Value $value does not meet the requirements for the parameter ${parameter.name}" }

        // Update the value of the parameter.
        parameterStates[parameter.name] = value
    }
}

internal data class KernelConfigurationResults(
    val kernelConfiguration: KernelConfiguration,
    val ioVectorSizes: MutableMap<String, Int>,
)
