package net.voxelpi.vire.engine.kernel.kotlin

import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelConfigurationException
import net.voxelpi.vire.engine.kernel.KernelImpl
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VariantVariable
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.provider.MutableVectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.MutableVectorSizeStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableVectorSizeStorageWrapper

public interface ConfigurationContext : VariableProvider, MutableVectorSizeProvider, ParameterStateProvider {

    /**
     * The kernel which of which the instance was created.
     */
    public val kernel: Kernel

    /**
     * Stops the configuration of the kernel instance.
     * This is intended to allow the kernel to signal an invalid parameter state.
     */
    public fun signalInvalidConfiguration(message: String = "Invalid kernel configuration"): Nothing {
        throw KernelConfigurationException(message)
    }

    /**
     * Declares a new variable on the kernel.
     * All variable kinds except for parameters can be declared here.
     */
    public fun <V : VariantVariable<*>> declare(variable: V): V
}

internal class ConfigurationContextImpl(
    override val kernel: KernelImpl,
    private val parameterStateProvider: ParameterStateProvider,
) : ConfigurationContext, MutableVectorSizeStorageWrapper {

    val variables: MutableMap<String, Variable<*>> = kernel.variables()
        .associateBy { it.name }
        .toMutableMap()

    override fun variables(): Collection<Variable<*>> {
        return variables.values
    }

    override fun variable(name: String): Variable<*>? {
        return variables[name]
    }

    override val vectorSizeStorage: MutableVectorSizeStorage = MutableVectorSizeStorage(
        this,
        kernel.vectorVariables()
            .associate { it.name to it.size.get(this) }
            .toMutableMap(),
    )

    override val variableProvider: VariableProvider
        get() = kernel

    override fun <V : VariantVariable<*>> declare(variable: V): V {
        require(variable.name !in variables) { "A variable with the name \"${variable.name}\" already exists" }
        variables[variable.name] = variable
        if (variable is VectorVariable<*>) {
            vectorSizeStorage.resize(variable, variable.size.get(this))
        }
        return variable
    }

    override fun <T> get(parameter: Parameter<T>): T {
        return parameterStateProvider[parameter]
    }
}
