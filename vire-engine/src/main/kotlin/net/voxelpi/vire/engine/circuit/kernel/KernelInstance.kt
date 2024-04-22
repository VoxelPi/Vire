package net.voxelpi.vire.engine.circuit.kernel

import net.voxelpi.vire.engine.circuit.kernel.variable.IOVector
import net.voxelpi.vire.engine.circuit.kernel.variable.Parameter

/**
 * An instance of a kernel.
 */
public interface KernelInstance {

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
     * Returns the size of the given [ioVector].
     */
    public fun size(ioVector: IOVector): Int

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

        public fun create(kernel: Kernel): KernelInstance {
            require(kernel is KernelImpl)
            return KernelInstanceImpl.create(kernel)
        }

        public fun create(kernel: Kernel, block: KernelConfiguration.() -> Unit): KernelInstance {
            require(kernel is KernelImpl)
            return KernelInstanceImpl.create(kernel, block)
        }

        public fun create(kernel: Kernel, values: Map<String, Any?>): KernelInstance {
            require(kernel is KernelImpl)
            return KernelInstanceImpl.create(kernel, values)
        }
    }
}

internal class KernelInstanceImpl(
    override val kernel: KernelImpl,
    configuration: KernelConfigurationImpl,
) : KernelInstance {

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
            require(parameterName in kernel.parameters) { "Specified value for unknown parameter '$parameterName'" }
            val parameter = kernel.parameters[parameterName]!!

            // Check that the value is valid for the parameter.
            require(parameter.isValidValue(parameterValue)) { "Invalid value for the parameter ${parameter.name}" }
            this[parameterName] = parameterValue
        }

        // Apply the configuration.
        return configure(configuration)
    }

    private fun configure(configuration: KernelConfigurationImpl): Result<Unit> {
        // Let the kernel process the configuration.
        val results = kernel.processConfiguration(configuration).getOrElse {
            return Result.failure(it)
        }

        // Update the instance state.
        parameterStates = configuration.parameterStates
        ioVectorSizes = results.ioVectorSizes
        return Result.success(Unit)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameter: Parameter<T>): T {
        // Check that a parameter with the given name exists.
        require(parameter.name in kernel.parameters) { "Unknown parameter ${parameter.name}" }

        // Return the value of the parameter.
        return parameterStates[parameter.name] as T
    }

    operator fun <T> set(parameter: Parameter<T>, value: T) {
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

    operator fun set(parameterName: String, value: Any?) {
        // Check that a parameter with the given name exists.
        require(parameterName in kernel.parameters) { "Unknown parameter $parameterName" }
        val parameter = kernel.parameters[parameterName]!!

        // Check that the value is valid for the specified parameter.
        require(parameter.isValidValue(value)) { "Value $value does not meet the requirements for the parameter ${parameter.name}" }

        // Update the value of the parameter.
        parameterStates[parameter.name] = value
    }

    override fun size(ioVector: IOVector): Int {
        // Check that the io vector is defined on the kernel.
        require(ioVector.name in kernel.inputs || ioVector.name in kernel.outputs)

        // Return the size.
        return ioVectorSizes[ioVector.name]!!
    }

    override fun size(variableName: String): Int {
        // Check that the io vector is defined on the kernel.
        require(variableName in kernel.inputs || variableName in kernel.outputs)

        // Return the size.
        return ioVectorSizes[variableName]!!
    }

    /**
     * Changes the size of the given [ioVector] to the given [size].
     */
    fun resize(ioVector: IOVector, size: Int) {
        // Check that the io vector is defined on the kernel.
        require(ioVector.name in kernel.inputs || ioVector.name in kernel.outputs)

        // Modify the size of the io vector.
        ioVectorSizes[ioVector.name] = size
    }

    /**
     * Changes the size of the given IO-vector with the given [variableName] to the given [size].
     */
    fun resize(variableName: String, size: Int) {
        // Check that the io vector is defined on the kernel.
        require(variableName in kernel.inputs || variableName in kernel.outputs)

        // Modify the size of the io vector.
        ioVectorSizes[variableName] = size
    }

    companion object {
        fun create(kernel: KernelImpl): KernelInstanceImpl {
            val instance = KernelInstanceImpl(kernel, kernel.generateDefaultConfiguration())
            return instance
        }

        fun create(kernel: KernelImpl, block: KernelConfiguration.() -> Unit): KernelInstanceImpl {
            val config = kernel.generateDefaultConfiguration()
            config.block()
            return KernelInstanceImpl(kernel, config)
        }

        fun create(kernel: KernelImpl, values: Map<String, Any?>): KernelInstanceImpl {
            val config = kernel.generateDefaultConfiguration()
            for ((parameterName, parameterValue) in values) {
                // Check that only existing parameters are specified.
                require(parameterName in kernel.parameters) { "Specified value for unknown parameter '$parameterName'" }
                val parameter = kernel.parameters[parameterName]!!

                // Check that the value is valid for the parameter.
                require(parameter.isValidValue(parameterValue)) { "Invalid value for the parameter ${parameter.name}" }
                config[parameterName] = parameterValue
            }
            return KernelInstanceImpl(kernel, config)
        }
    }
}
