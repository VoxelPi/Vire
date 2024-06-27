package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.builder.ConfigurationContext
import net.voxelpi.vire.engine.kernel.builder.ConfigurationContextImpl
import net.voxelpi.vire.engine.kernel.builder.InitializationContext
import net.voxelpi.vire.engine.kernel.builder.InitializationContextImpl
import net.voxelpi.vire.engine.kernel.builder.KernelBuilder
import net.voxelpi.vire.engine.kernel.builder.KernelBuilderImpl
import net.voxelpi.vire.engine.kernel.builder.UpdateContext
import net.voxelpi.vire.engine.kernel.builder.UpdateContextImpl
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.generateInitialFieldStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.generateInitialOutputStateStorage

/**
 * A kernel is logical processor that produces logical outputs from its inputs and other parameters.
 */
public interface Kernel : VariableProvider {

    /**
     * The tags of the kernel.
     */
    public val tags: Set<Identifier>

    /**
     * The properties of the kernel.
     */
    public val properties: Map<Identifier, String>

    /**
     * Creates a new variant of the kernel using the default value of each parameter.
     *
     * @param base A parameter state provider that should be used to initialize all parameters.
     */
    public fun createVariant(
        base: ParameterStateProvider = generateDefaultParameterStates(),
    ): Result<KernelVariant>

    /**
     * Creates a new variant of the kernel using the given [lambda] to initialize the parameters.
     * Before the builder is run, all parameters of the kernel are initialized to their default values,
     * therefore the builder doesn't have to set every parameter.
     *
     * @param base A parameter state provider that should be used to initialize all parameters before the lambda is run.
     * @param lambda the receiver lambda which will be invoked on the builder.
     */
    public fun createVariant(
        base: ParameterStateProvider = generateDefaultParameterStates(),
        lambda: KernelVariantBuilder.() -> Unit,
    ): Result<KernelVariant>

    /**
     * Creates a new variant of the kernel using the given [values] as the state of the parameters.
     * The value map doesn't have to contain entries for every parameter,
     * parameters without specified value are set to their default value.
     * However, the value map must not have any entries for parameters that do not belong to the kernel.
     *
     * @param base A parameter state provider that should be used to initialize all parameters that are not specified in the map.
     * @param values the values that should be applied to the kernel configuration.
     */
    public fun createVariant(
        values: ParameterStateMap,
        base: ParameterStateProvider = generateDefaultParameterStates(),
    ): Result<KernelVariant>

    /**
     * Generates a new [ParameterStateProvider] with the default value of each parameter.
     */
    public fun generateDefaultParameterStates(): ParameterStateProvider
}

/**
 * Creates a new [Kernel] using the given [lambda].
 */
public fun kernel(lambda: KernelBuilder.() -> Unit): Kernel {
    val builder = KernelBuilderImpl()
    builder.lambda()
    return builder.build()
}

internal open class KernelImpl(
    override val tags: Set<Identifier>,
    override val properties: Map<Identifier, String>,
    val variables: Map<String, Variable<*>>,
    val configurationAction: (ConfigurationContext) -> Unit,
    val initializationAction: (InitializationContext) -> Unit,
    val updateAction: (UpdateContext) -> Unit,
) : Kernel {

    override fun variables(): Collection<Variable<*>> {
        return variables.values
    }

    override fun variable(name: String): Variable<*>? {
        return variables[name]
    }

    override fun createVariant(base: ParameterStateProvider): Result<KernelVariantImpl> {
        val config = KernelVariantConfig(this, base)
        return generateVariant(config)
    }

    override fun createVariant(base: ParameterStateProvider, lambda: KernelVariantBuilder.() -> Unit): Result<KernelVariantImpl> {
        val config = KernelVariantBuilderImpl(this, base).apply(lambda).build()
        return generateVariant(config)
    }

    override fun createVariant(values: ParameterStateMap, base: ParameterStateProvider): Result<KernelVariantImpl> {
        val config = KernelVariantBuilderImpl(this, base).update(values).build()
        return generateVariant(config)
    }

    fun generateVariant(config: KernelVariantConfig): Result<KernelVariantImpl> {
        // Check that all parameters have been assigned a value.
        for (parameter in parameters()) {
            if (!config.hasValue(parameter)) {
                throw IllegalArgumentException("Incomplete kernel configuration, parameter \"${parameter.name}\" has no value set")
            }
        }

        // Start kernel configuration phase.
        val context = ConfigurationContextImpl(this, config)
        try {
            configurationAction(context)
        } catch (exception: KernelConfigurationException) {
            return Result.failure(exception)
        }

        // Build variant.
        val variant = KernelVariantImpl(
            this,
            context.variables,
            config.parameterStateStorage,
            context.vectorSizeStorage.copy(),
        )
        return Result.success(variant)
    }

    fun generateInstance(config: KernelInstanceConfig): Result<KernelInstanceImpl> {
        val kernelVariant = config.kernelVariant

        // Check that all settings have been assigned a value.
        for (setting in settings()) {
            if (!config.hasValue(setting)) {
                throw IllegalArgumentException("Incomplete kernel configuration, setting \"${setting.name}\" has no value set")
            }
        }

        // Generate initial field states.
        val fieldStateStorage = generateInitialFieldStateStorage(kernelVariant, config)

        // Generate initial output states.
        val outputStateStorage = generateInitialOutputStateStorage(kernelVariant, config)

        // Initialize the kernel instance.
        val context = InitializationContextImpl(config.kernelVariant, config.settingStateStorage, fieldStateStorage, outputStateStorage)
        try {
            initializationAction(context)
        } catch (exception: KernelInitializationException) {
            return Result.failure(exception)
        }

        // Check that all fields have been assigned a value.
        for (field in fields()) {
            if (!fieldStateStorage.hasValue(field)) {
                throw IllegalArgumentException("Incomplete kernel initialization, field \"${field.name}\" has not been initialized")
            }
        }

        // Create the kernel instance.
        val instance = KernelInstanceImpl(
            config.kernelVariant,
            config.settingStateStorage,
            fieldStateStorage.copy(),
            outputStateStorage.copy(),
        )
        return Result.success(instance)
    }

    fun updateKernel(state: MutableKernelState) {
        val context = UpdateContextImpl(state)
        updateAction(context)
    }

    override fun generateDefaultParameterStates(): KernelVariantConfig {
        val parameterStates = mutableMapOf<String, Any?>()
        for (parameter in parameters()) {
            val initialization = parameter.initialization ?: continue
            parameterStates[parameter.name] = initialization.invoke()
        }
        return KernelVariantConfig(this, parameterStates)
    }
}
