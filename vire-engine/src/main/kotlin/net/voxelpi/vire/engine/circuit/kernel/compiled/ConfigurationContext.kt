package net.voxelpi.vire.engine.circuit.kernel.compiled

import net.voxelpi.vire.engine.circuit.kernel.InvalidKernelConfigurationException
import net.voxelpi.vire.engine.circuit.kernel.Kernel
import net.voxelpi.vire.engine.circuit.kernel.variable.IOVector
import net.voxelpi.vire.engine.circuit.kernel.variable.Parameter

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
        throw InvalidKernelConfigurationException(message)
    }
}
