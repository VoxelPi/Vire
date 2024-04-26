package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.Setting

/**
 * An instance of a kernel.
 */
public interface KernelInitialization {

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
     * Returns the current value of the given [setting].
     *
     * @param setting the parameter of which the value should be returned.
     */
    public operator fun <T> get(setting: Setting<T>): T

    /**
     * Sets the value of the given [setting] to the given [value].
     *
     * @param setting the parameter of which the value should be modified.
     * @param value the new value of the parameter.
     */
    public operator fun <T> set(setting: Setting<T>, value: T)

    /**
     * Returns the current value of the parameter with the given [variableName].
     *
     * @param variableName the name of the parameter of which the value should be returned.
     */
    public operator fun get(variableName: String): Any?

    /**
     * Sets the value of the parameter with the given [parameterName] to the given [value].
     *
     * @param parameterName the name of the parameter of which the value should be modified.
     * @param value the new value of the parameter.
     */
    public operator fun set(parameterName: String, value: Any?)
}
