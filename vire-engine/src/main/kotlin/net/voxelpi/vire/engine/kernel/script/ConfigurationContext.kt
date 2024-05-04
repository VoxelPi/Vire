package net.voxelpi.vire.engine.kernel.script

import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelConfigurationException
import net.voxelpi.vire.engine.kernel.variable.MutableVectorVariableSizeMap
import net.voxelpi.vire.engine.kernel.variable.MutableVectorVariableSizeProvider
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider

public interface ConfigurationContext : MutableVectorVariableSizeProvider, ParameterStateProvider {

    /**
     * The kernel which of which the instance was created.
     */
    public val kernel: Kernel

    /**
     * Stops the configuration of the kernel instance.
     * This is intended to allow the kernel to signal an invalid parameter state.
     */
    public fun signalInvalidConfiguration(message: String = ""): Nothing {
        throw KernelConfigurationException(message)
    }
}

internal class ConfigurationContextImpl(
    override val kernel: Kernel,
    val parameterStateProvider: ParameterStateProvider,
) : ConfigurationContext, MutableVectorVariableSizeMap {

    override val vectorVariableSizes: MutableMap<String, Int> = kernel.vectorVariables()
        .associate { it.name to it.size.get(this) }
        .toMutableMap()

    override fun <T> get(parameter: Parameter<T>): T {
        return parameterStateProvider[parameter]
    }
}
