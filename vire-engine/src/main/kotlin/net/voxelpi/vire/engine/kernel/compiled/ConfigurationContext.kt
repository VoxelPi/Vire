package net.voxelpi.vire.engine.kernel.compiled

import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelConfigurationException
import net.voxelpi.vire.engine.kernel.KernelConfigurationImpl
import net.voxelpi.vire.engine.kernel.variable.IOVector
import net.voxelpi.vire.engine.kernel.variable.Parameter

public interface ConfigurationContext {

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
     * Changes the size of the given [ioVector] to the given [size].
     */
    public fun resize(ioVector: IOVector, size: Int)

    /**
     * Changes the size of the given IO-vector with the given [variableName] to the given [size].
     */
    public fun resize(variableName: String, size: Int)

    /**
     * Stops the configuration of the kernel instance.
     * This is intended to allow the kernel to signal an invalid parameter state.
     */
    public fun signalInvalidConfiguration(message: String = ""): Nothing {
        throw KernelConfigurationException(message)
    }
}

internal class ConfigurationContextImpl(
    val configuration: KernelConfigurationImpl,
) : ConfigurationContext {

    override val kernel: Kernel
        get() = configuration.kernel

    val ioVectorSizes: MutableMap<String, Int> = mutableMapOf()

    init {
        for (input in kernel.inputs()) {
            ioVectorSizes[input.name] = input.initialSize.provideValue()
        }
        for (output in kernel.outputs()) {
            ioVectorSizes[output.name] = output.initialSize.provideValue()
        }
    }

    override fun <T> get(parameter: Parameter<T>): T {
        return configuration[parameter]
    }

    override fun get(parameterName: String): Any? {
        return configuration[parameterName]
    }

    override fun size(ioVector: IOVector): Int {
        // Check that the io vector is defined on the kernel.
        require(kernel.hasInput(ioVector.name) || kernel.hasOutput(ioVector.name)) { "Unknown IO Vector ${ioVector.name}" }

        // Return the size.
        return ioVectorSizes[ioVector.name]!!
    }

    override fun size(variableName: String): Int {
        // Check that the io vector is defined on the kernel.
        require(kernel.hasInput(variableName) || kernel.hasOutput(variableName)) { "Unknown IO Vector $variableName" }

        // Return the size.
        return ioVectorSizes[variableName]!!
    }

    override fun resize(ioVector: IOVector, size: Int) {
        // Check that the io vector is defined on the kernel.
        require(kernel.hasInput(ioVector.name) || kernel.hasOutput(ioVector.name)) { "Unknown IO Vector ${ioVector.name}" }

        // Check that the size of the variable is greater than 0.
        require(size >= 0) { "IO Vector size must be greater than or equal to zero" }

        // Return the size.
        ioVectorSizes[ioVector.name] = size
    }

    override fun resize(variableName: String, size: Int) {
        // Check that the io vector is defined on the kernel.
        require(kernel.hasInput(variableName) || kernel.hasOutput(variableName)) { "Unknown IO Vector $variableName" }

        // Check that the size of the variable is greater than 0.
        require(size >= 0) { "IO Vector size must be greater than or equal to zero" }

        // Return the size.
        ioVectorSizes[variableName] = size
    }
}
