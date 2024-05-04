package net.voxelpi.vire.engine.kernel.script

import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.variable.Parameter

public interface InitializationContext {

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
}
