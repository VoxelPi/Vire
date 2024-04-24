package net.voxelpi.vire.engine.circuit.kernel

import net.voxelpi.vire.engine.circuit.kernel.variable.IOVector
import net.voxelpi.vire.engine.circuit.kernel.variable.IOVectorSizeProvider
import net.voxelpi.vire.engine.circuit.kernel.variable.Input
import net.voxelpi.vire.engine.circuit.kernel.variable.Output
import net.voxelpi.vire.engine.circuit.kernel.variable.Parameter
import net.voxelpi.vire.engine.circuit.kernel.variable.ParameterStateProvider

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
     * Updates the state of the parameters of the instance using the given [block].
     */
    public fun configure(block: KernelConfiguration.() -> Unit): Result<Unit>

    /**
     * Updates the state of the parameters of the instance to the given [values].
     */
    public fun configure(values: Map<String, Any?>): Result<Unit>

    public companion object {

        /**
         * Creates a new instance of the given [kernel].
         * @param kernel the kernel of which a new instance should be created.
         */
        public fun create(kernel: Kernel): KernelVariant {
            require(kernel is KernelImpl)
            return KernelVariantImpl.create(kernel)
        }

        /**
         * Creates a new instance of the given [kernel] that is configured using the given [block].
         * Values are initialized to their default values before the block is applied.
         * @param kernel the kernel of which a new instance should be created.
         * @param block the code that should be applied to the kernel configuration.
         */
        public fun create(kernel: Kernel, block: KernelConfiguration.() -> Unit): KernelVariant {
            require(kernel is KernelImpl)
            return KernelVariantImpl.create(kernel, block)
        }

        /**
         * Creates a new instance of the given [kernel] that is configured using the given [values].
         * Values are initialized to their default values if not specified in the values map.
         * @param kernel the kernel of which a new instance should be created.
         * @param values the values that should be applied to the kernel configuration.
         */
        public fun create(kernel: Kernel, values: Map<String, Any?>): KernelVariant {
            require(kernel is KernelImpl)
            return KernelVariantImpl.create(kernel, values)
        }
    }
}

internal class KernelVariantImpl(
    override val kernel: KernelImpl,
    configuration: KernelConfigurationImpl,
) : KernelVariant {

    private var parameterStates: MutableMap<String, Any?> = mutableMapOf()

    private var ioVectorSizes: MutableMap<String, Int> = mutableMapOf()

    init {
        configure(configuration).getOrThrow()
    }

    override fun configure(block: KernelConfiguration.() -> Unit): Result<Unit> {
        // Create a new configuration from the current instance state and apply the update block.
        val configuration = KernelConfigurationImpl(kernel, parameterStates.toMutableMap())
        configuration.block()

        // Apply the configuration.
        return configure(configuration)
    }

    override fun configure(values: Map<String, Any?>): Result<Unit> {
        // Create a new configuration from the current instance state and apply the update block.
        val configuration = KernelConfigurationImpl(kernel, parameterStates.toMutableMap())
        for ((parameterName, parameterValue) in values) {
            // Check that only existing parameters are specified.
            val parameter = kernel.parameter(parameterName)
                ?: throw IllegalArgumentException("Unknown parameter '$parameterName'")

            // Check that the value is valid for the parameter.
            require(parameter.isValidValue(parameterValue)) { "Invalid value for the parameter ${parameter.name}" }
            this[parameterName] = parameterValue
        }

        // Apply the configuration.
        return configure(configuration)
    }

    private fun configure(configuration: KernelConfigurationImpl): Result<Unit> {
        // Let the kernel process the configuration.
        val results = kernel.configureKernel(configuration).getOrElse {
            return Result.failure(it)
        }

        // Update the instance state.
        parameterStates = configuration.parameterStates
        ioVectorSizes = results.ioVectorSizes.toMutableMap()
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

    companion object {
        fun create(kernel: KernelImpl): KernelVariantImpl {
            val instance = KernelVariantImpl(kernel, kernel.generateDefaultConfiguration())
            return instance
        }

        fun create(kernel: KernelImpl, block: KernelConfiguration.() -> Unit): KernelVariantImpl {
            val config = kernel.generateDefaultConfiguration()
            config.block()
            return KernelVariantImpl(kernel, config)
        }

        fun create(kernel: KernelImpl, values: Map<String, Any?>): KernelVariantImpl {
            val config = kernel.generateDefaultConfiguration()
            for ((parameterName, parameterValue) in values) {
                // Check that only existing parameters are specified.
                val parameter = kernel.parameter(parameterName)
                    ?: throw IllegalArgumentException("Unknown parameter '$parameterName'")

                // Check that the value is valid for the parameter.
                require(parameter.isValidValue(parameterValue)) { "Invalid value for the parameter ${parameter.name}" }
                config[parameterName] = parameterValue
            }
            return KernelVariantImpl(kernel, config)
        }
    }
}
