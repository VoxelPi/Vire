package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.MutableSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariableSizeProvider

/**
 * An instance of a kernel.
 */
public interface KernelInstanceBuilder : ParameterStateProvider, VectorVariableSizeProvider, MutableSettingStateProvider {

    /**
     * The kernel which of which the instance was created.
     */
    public val kernel: Kernel

    /**
     * Returns the current value of the parameter with the given [variableName].
     *
     * @param variableName the name of the parameter of which the value should be returned.
     */
    public operator fun get(variableName: String): Any?

    /**
     * Sets the value of the variable with the given [variableName] to the given [value].
     *
     * @param variableName the name of the parameter of which the value should be modified.
     * @param value the new value of the parameter.
     */
    public operator fun set(variableName: String, value: Any?)
}
