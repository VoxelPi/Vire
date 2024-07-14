package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.SettingInitializationContext
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.patch.SettingStatePatch
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.VectorSizeStorage
import net.voxelpi.vire.engine.kernel.variable.storage.VectorSizeStorageWrapper
import java.util.Objects

/**
 * An instance of a kernel.
 */
public interface KernelVariant : VariableProvider, ParameterStateProvider, VectorSizeProvider {

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
     * Creates a new variant of the kernel variant using the given [lambda] to initialize the settings.
     * Before the lambda is run, all settings of the kernel variant are initialized to their default values,
     * therefore the lambda doesn't have to set a setting.
     *
     * @param base A setting state provider that should be used to initialize all settings before the lambda is run.
     * @param lambda the receiver lambda which will be invoked on the builder.
     */
    public fun createInstance(
        base: PartialSettingStateProvider = generateDefaultSettingStates(),
        lambda: KernelInstanceBuilder.() -> Unit = {},
    ): Result<KernelInstance>

    /**
     * Creates a new instance of the kernel variant using the given [values] as the state of the settings.
     * The value map doesn't have to contain entries for every setting,
     * settings without specified value are set to their default value.
     * However, the value map must not have any entries for settings that do not belong to the kernel variant.
     *
     * @param base A setting state provider that should be used to initialize all parameters that are not specified in the map.
     * @param values the values that should be applied to the kernel configuration.
     */
    public fun createInstance(
        values: SettingStateMap,
        base: PartialSettingStateProvider = generateDefaultSettingStates(),
    ): Result<KernelInstance>

    /**
     * Generates a new [SettingStateProvider] with the default value of each setting.
     */
    public fun generateDefaultSettingStates(): PartialSettingStateProvider

    /**
     * Generates a new [SettingInitializationContext] for the initialization of all settings.
     */
    public fun settingInitializationContext(): SettingInitializationContext
}

internal class KernelVariantImpl(
    override val kernel: Kernel,
    val variables: Map<String, Variable<*>>,
    override val parameterStateStorage: ParameterStateStorage,
    override val vectorSizeStorage: VectorSizeStorage,
) : KernelVariant, VectorSizeStorageWrapper, ParameterStateStorageWrapper {

    override val variableProvider: VariableProvider
        get() = this

    override fun variables(): Collection<Variable<*>> {
        return variables.values
    }

    override fun variable(name: String): Variable<*>? {
        return variables[name]
    }

    override fun copy(): Result<KernelVariant> {
        return kernel.createVariant(this)
    }

    override fun copy(lambda: KernelVariantBuilder.() -> Unit): Result<KernelVariant> {
        return kernel.createVariant(this, lambda)
    }

    override fun copy(values: Map<String, Any?>): Result<KernelVariant> {
        return kernel.createVariant(this) {
            for ((key, value) in values) {
                this[key] = value
            }
        }
    }

    override fun get(parameterName: String): Any? {
        // Check that a parameter with the given name exists.
        val parameter = kernel.parameter(parameterName)
        require(parameter != null) { "Unknown parameter $parameterName" }

        // Return the value of the parameter.
        return this[parameter]
    }

    override fun createInstance(base: PartialSettingStateProvider, lambda: KernelInstanceBuilder.() -> Unit): Result<KernelInstanceImpl> {
        val config = KernelInstanceBuilderImpl(this, base).apply(lambda).build()
        return kernel.generateInstance(config)
    }

    override fun createInstance(values: SettingStateMap, base: PartialSettingStateProvider): Result<KernelInstanceImpl> {
        val config = KernelInstanceBuilderImpl(this, base).apply(values).build()
        return kernel.generateInstance(config)
    }

    override fun generateDefaultSettingStates(): SettingStatePatch {
        val settingInitializationContext = settingInitializationContext()
        val settingStates = mutableMapOf<String, Any?>()
        for (setting in settings()) {
            val initialization = setting.initialization ?: continue
            settingStates[setting.name] = initialization.invoke(settingInitializationContext)
        }
        return SettingStatePatch(this, settingStates)
    }

    override fun settingInitializationContext(): SettingInitializationContext {
        return SettingInitializationContext(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KernelVariantImpl

        if (this.kernel != other.kernel) return false
        if (this.parameterStateStorage != other.parameterStateStorage) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(kernel, parameterStateStorage)
    }
}
