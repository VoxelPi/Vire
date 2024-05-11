package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.FieldProvider
import net.voxelpi.vire.engine.kernel.variable.InputProvider
import net.voxelpi.vire.engine.kernel.variable.OutputProvider
import net.voxelpi.vire.engine.kernel.variable.ParameterProvider
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.ParameterStateStorage
import net.voxelpi.vire.engine.kernel.variable.ParameterStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.SettingProvider
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.VectorSizeStorage
import net.voxelpi.vire.engine.kernel.variable.VectorSizeStorageWrapper

/**
 * An instance of a kernel.
 */
public interface KernelVariant :
    VariableProvider,
    ParameterProvider,
    SettingProvider,
    FieldProvider,
    InputProvider,
    OutputProvider,
    ParameterStateProvider,
    VectorSizeProvider {

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
     * Creates a new copy of this kernel variant.
     */
    public fun copy(): Result<KernelVariant>

    /**
     * Creates a new copy of this kernel variant, whose parameters have been modified using the given [lambda].
     */
    public fun copy(lambda: KernelVariantBuilder.() -> Unit): Result<KernelVariant>

    /**
     * Creates a new copy of this kernel variant, whose parameters have been modified using the given [values].
     */
    public fun copy(values: Map<String, Any?>): Result<KernelVariant>

    /**
     * Creates a new instance of the kernel variant.
     */
    public fun createInstance(
        base: SettingStateProvider = generateDefaultSettingStates(),
    ): Result<KernelInstance>

    /**
     * Generates a new [SettingStateProvider] with the default value of each setting.
     */
    public fun generateDefaultSettingStates(): SettingStateProvider
}

internal class KernelVariantImpl(
    override val kernel: KernelImpl,
    val variables: Map<String, Variable<*>>,
    override val parameterStateStorage: ParameterStateStorage,
    override val vectorSizeStorage: VectorSizeStorage,
) : KernelVariant, VectorSizeStorageWrapper, ParameterStateStorageWrapper {

    override fun variables(): Collection<Variable<*>> {
        return variables.values
    }

    override fun variable(name: String): Variable<*>? {
        return variables[name]
    }

    override fun copy(): Result<KernelVariantImpl> {
        return kernel.createVariant(this)
    }

    override fun copy(lambda: KernelVariantBuilder.() -> Unit): Result<KernelVariantImpl> {
        return kernel.createVariant(this, lambda)
    }

    override fun copy(values: Map<String, Any?>): Result<KernelVariantImpl> {
        return kernel.createVariant(values, this)
    }

    override fun get(parameterName: String): Any? {
        // Check that a parameter with the given name exists.
        val parameter = kernel.parameter(parameterName)
        require(parameter != null) { "Unknown parameter $parameterName" }

        // Return the value of the parameter.
        return this[parameter]
    }

    override fun createInstance(base: SettingStateProvider): Result<KernelInstance> {
        val config = KernelInstanceConfig(this, base)
        return kernel.generateInstance(config)
    }

    override fun generateDefaultSettingStates(): SettingStateProvider {
        val settingStates = mutableMapOf<String, Any?>()
        for (setting in settings()) {
            settingStates[setting.name] = setting.initialization()
        }
        return KernelInstanceConfig(this, settingStates)
    }
}
