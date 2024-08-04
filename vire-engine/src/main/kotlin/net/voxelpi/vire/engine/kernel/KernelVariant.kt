package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.patch.MutableSettingStatePatch
import net.voxelpi.vire.engine.kernel.variable.patch.SettingStatePatch
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateStorageWrapper
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
     * Creates a new copy of this kernel variant, whose parameters have been modified using the given [lambda].
     */
    public fun copy(lambda: KernelVariantBuilder.() -> Unit = {}): Result<KernelVariant>

    /**
     * Creates a new copy of this kernel variant, whose parameters have been modified using the given [values].
     */
    public fun copy(values: Map<String, Any?>): Result<KernelVariant>

    /**
     * Creates a new variant of the kernel variant using the given [lambda] to initialize the settings.
     * Before the lambda is run, all settings of the kernel variant are initialized to their default values,
     * therefore the lambda doesn't have to set a setting.
     *
     * @param patches Partial setting state providers that should be used to initialize all settings before the lambda is run.
     * @param lambda the receiver lambda which will be invoked on the builder.
     */
    public fun createInstance(
        vararg patches: PartialSettingStateProvider,
        lambda: KernelInstanceBuilder.() -> Unit = {},
    ): Result<KernelInstance>
}

internal class KernelVariantImpl(
    override val kernel: Kernel,
    parameterStateProvider: ParameterStateProvider,
    override val variableProvider: VariableProvider,
    vectorSizeProvider: VectorSizeProvider,
    initialSettingStateProvider: PartialSettingStateProvider,
) : KernelVariant, VectorSizeStorageWrapper, ParameterStateStorageWrapper {

    override val parameterStateStorage: ParameterStateStorage = ParameterStateStorage(this, parameterStateProvider)
    override val vectorSizeStorage: VectorSizeStorage = VectorSizeStorage(this, vectorSizeProvider)
    val initialSettingStatePatch: SettingStatePatch = SettingStatePatch(this, initialSettingStateProvider)

    override fun variables(): Collection<Variable<*>> {
        return variableProvider.variables()
    }

    override fun variable(name: String): Variable<*>? {
        return variableProvider.variable(name)
    }

    override fun copy(lambda: KernelVariantBuilder.() -> Unit): Result<KernelVariant> {
        return kernel.createVariant(this, lambda = lambda)
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

    override fun createInstance(
        vararg patches: PartialSettingStateProvider,
        lambda: KernelInstanceBuilder.() -> Unit,
    ): Result<KernelInstance> {
        // Build initial setting state patch.
        val settingStates = MutableSettingStatePatch(this, variableProvider.defaultSettingStates(this, this))
        settingStates.applySettingStatePatch(initialSettingStatePatch)
        patches.forEach(settingStates::applySettingStatePatch)

        // Create builder and apply lambda to create the instance config.
        val builder = KernelInstanceBuilderImpl(this, settingStates)
        builder.lambda()
        val config = builder.build()

        // Generate instance data.
        val data = kernel.createInstanceData(
            variableProvider,
            vectorSizeStorage,
            parameterStateStorage,
            config.settingStateProvider,
        ).getOrElse { return Result.failure(it) }

        // Create the kernel.
        return Result.success(
            KernelInstanceImpl(
                this,
                data.settingStateProvider,
                data.fieldStateProvider,
                data.outputStateProvider,
            )
        )
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
