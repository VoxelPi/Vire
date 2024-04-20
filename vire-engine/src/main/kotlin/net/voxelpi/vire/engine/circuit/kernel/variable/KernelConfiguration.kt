package net.voxelpi.vire.engine.circuit.kernel.variable

import net.voxelpi.vire.engine.circuit.kernel.Kernel

public interface KernelConfiguration {

    /**
     * The kernel which is being configured.
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
    public operator fun <T> get(parameterName: String): T

    public companion object {

        public fun create(kernel: Kernel, configuration: KernelConfigurationContext.() -> Unit): KernelConfiguration {
            return KernelConfigurationImpl.create(kernel, configuration)
        }

        public fun create(kernel: Kernel, configuration: Map<String, Any?>): KernelConfiguration {
            return KernelConfigurationImpl.create(kernel, configuration)
        }
    }
}

/**
 * Provides method that are available during the configuration of a kernel.
 */
public sealed interface KernelConfigurationContext {

    /**
     * The kernel which is being configured.
     */
    public val kernel: Kernel

    /**
     * Returns the current value of the given [parameter].
     *
     * @param parameter the parameter of which the value should be returned.
     */
    public operator fun <T> get(parameter: Parameter<T>): T

    /**
     * Sets the value of the given [parameter] to the given [value].
     *
     * @param parameter the parameter of which the value should be modified.
     * @param value the new value of the parameter.
     */
    public operator fun <T> set(parameter: Parameter<T>, value: T)

    /**
     * Returns the current value of the parameter with the given [parameterName].
     *
     * @param parameterName the name of the parameter of which the value should be returned.
     */
    public operator fun <T> get(parameterName: String): T

    /**
     * Sets the value of the parameter with the given [parameterName] to the given [value].
     *
     * @param parameterName the name of the parameter of which the value should be modified.
     * @param value the new value of the parameter.
     */
    public operator fun <T> set(parameterName: String, value: T)
}

internal class KernelConfigurationImpl(
    override val kernel: Kernel,
    val parameterStates: Map<String, Any?>,
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
    override operator fun <T> get(parameter: Parameter<T>): T {
        return parameterStates[parameter.name] as T
    }

    @Suppress("UNCHECKED_CAST")
    override operator fun <T> get(parameterName: String): T {
        // Check that a parameter with the given name exists.
        require(parameterName in kernel.parameters) { "Unknown parameter $parameterName" }
        return parameterStates[parameterName] as T
    }

    companion object {

        fun create(kernel: Kernel, configuration: KernelConfigurationContext.() -> Unit): KernelConfiguration {
            // Generate parameter states
            val parameterStates = generateDefaultParameterStates(kernel)

            // Run provided configuration
            val configurationContext = KernelConfigurationContextImpl(kernel, parameterStates)
            configurationContext.configuration()

            // Return the configuration.
            return KernelConfigurationImpl(kernel, parameterStates)
        }

        fun create(kernel: Kernel, configuration: Map<String, Any?>): KernelConfiguration {
            // Generate parameter states
            val parameterStates = generateDefaultParameterStates(kernel)

            // Set the provided values
            for ((parameterName, parameterValue) in configuration) {
                // Check that only existing parameters are specified.
                require(parameterName in kernel.parameters) { "Specified value for unknown parameter '$parameterName'" }
                val parameter = kernel.parameters[parameterName]!!

                // Check that the value is valid for the parameter.
                require(parameter.isValidValue(parameterValue)) { "Invalid value for the parameter ${parameter.name}" }
                parameterStates[parameterName] = parameterValue
            }

            // Return the configuration.
            return KernelConfigurationImpl(kernel, parameterStates)
        }

        /**
         * Returns a map that contains all parameters of a kernel and their default value.
         */
        private fun generateDefaultParameterStates(kernel: Kernel): MutableMap<String, Any?> {
            // Generate map with default initial values.
            val parameterStates = mutableMapOf<String, Any?>()
            for (parameter in kernel.parameters()) {
                parameterStates[parameter.name] = parameter.initialization.provideValue()
            }
            return parameterStates
        }
    }
}

internal class KernelConfigurationContextImpl(
    override val kernel: Kernel,
    private val parameterStates: MutableMap<String, Any?>,
) : KernelConfigurationContext {

    @Suppress("UNCHECKED_CAST")
    override operator fun <T> get(parameter: Parameter<T>): T {
        return parameterStates[parameter.name] as T
    }

    override operator fun <T> set(parameter: Parameter<T>, value: T) {
        // Check that the value is valid for the specified parameter.
        require(parameter.isValidValue(value)) { "Value $value does not meet the requirements for the parameter ${parameter.name}" }
        parameterStates[parameter.name] = value
    }

    @Suppress("UNCHECKED_CAST")
    override operator fun <T> get(parameterName: String): T {
        // Check that a parameter with the given name exists.
        require(parameterName in kernel.parameters) { "Unknown parameter $parameterName" }
        return parameterStates[parameterName] as T
    }

    override operator fun <T> set(parameterName: String, value: T) {
        // Check that a parameter with the given name exists.
        require(parameterName in kernel.parameters) { "Unknown parameter $parameterName" }
        val parameter = kernel.parameters[parameterName]!!

        // Check that the value is valid for the specified parameter.
        require(parameter.isValidValue(value)) { "Value $value does not meet the requirements for the parameter ${parameter.name}" }
        parameterStates[parameter.name] = value
    }
}
