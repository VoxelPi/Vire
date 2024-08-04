package net.voxelpi.vire.engine.kernel.procedual

import net.voxelpi.vire.engine.kernel.KernelConfigurationException
import net.voxelpi.vire.engine.kernel.variable.MutableVariableRegistry
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.SettingInitializationContext
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VariantVariable
import net.voxelpi.vire.engine.kernel.variable.VectorSizeInitializationContext
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.patch.MutableSettingStatePatch
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialSettingStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.MutableVectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.storage.MutableVectorSizeStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableVectorSizeStorageWrapper

public interface ConfigurationContext : VariableProvider, MutableVectorSizeProvider, ParameterStateProvider {

    /**
     * The kernel which of which the instance was created.
     */
    public val kernel: ProceduralKernel

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
    override val kernel: ProceduralKernelImpl,
    override val parameterStateProvider: ParameterStateProvider,
) : ConfigurationContext, MutableVectorSizeStorageWrapper, ParameterStateProviderWrapper, MutablePartialSettingStateProviderWrapper {

    val variableStorage: MutableVariableRegistry = MutableVariableRegistry(kernel.variables())

    override fun variables(): Collection<Variable<*>> = variableStorage.variables()

    override fun variable(name: String): Variable<*>? = variableStorage.variable(name)

    override val settingStateProvider: MutableSettingStatePatch = MutableSettingStatePatch(
        this,
        variableStorage.defaultSettingStates(this, this),
    )

    private val vectorSizeInitializationContext = VectorSizeInitializationContext(parameterStateProvider)

    override val vectorSizeStorage: MutableVectorSizeStorage = MutableVectorSizeStorage(
        this,
        kernel.vectorVariables()
            .associate { it.name to it.size(vectorSizeInitializationContext) }
            .toMutableMap(),
    )

    override val variableProvider: VariableProvider
        get() = kernel

    @Suppress("UNCHECKED_CAST")
    override fun <V : VariantVariable<*>> declare(variable: V): V {
        variableStorage.declare(variable)

        // Update vector size storage if variable is a vector.
        if (variable is VectorVariable<*>) {
            vectorSizeStorage.resize(variable, variable.size(vectorSizeInitializationContext))
        }

        // Update setting state patch if variable is a setting with an initialization.
        if (variable is Setting<*>) {
            val initialization = variable.initialization
            if (initialization != null) {
                val context = SettingInitializationContext(this, this, this)
                settingStateProvider[variable as Setting<Any?>] = initialization.invoke(context)
            }
        }

        // Return the created variable.
        return variable
    }
}
