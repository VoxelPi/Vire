package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.script.ScriptKernel
import net.voxelpi.vire.engine.kernel.script.ScriptKernelBuilder
import net.voxelpi.vire.engine.kernel.script.ScriptKernelBuilderImpl
import net.voxelpi.vire.engine.kernel.variable.FieldProvider
import net.voxelpi.vire.engine.kernel.variable.InputProvider
import net.voxelpi.vire.engine.kernel.variable.OutputProvider
import net.voxelpi.vire.engine.kernel.variable.ParameterProvider
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.SettingProvider
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

/**
 * A kernel is logical processor that produces logical outputs from its inputs and other parameters.
 */
public interface Kernel : VariableProvider, ParameterProvider, SettingProvider, FieldProvider, InputProvider, OutputProvider {

    /**
     * The id of the kernel.
     */
    public val id: Identifier

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
        values: Map<String, Any?>,
        base: ParameterStateProvider = generateDefaultParameterStates(),
    ): Result<KernelVariant>

    /**
     * Generates a new [ParameterStateProvider] with the default value of each parameter.
     */
    public fun generateDefaultParameterStates(): ParameterStateProvider
}

/**
 * Creates a new [ScriptKernel] with the given [id] using the given [lambda].
 */
public fun kernel(id: Identifier, lambda: ScriptKernelBuilder.() -> Unit): ScriptKernel {
    val builder = ScriptKernelBuilderImpl(id)
    builder.lambda()
    return builder.build()
}

internal abstract class KernelImpl(
    override val id: Identifier,
    override val tags: Set<Identifier>,
    override val properties: Map<Identifier, String>,
) : Kernel {

    protected abstract val variables: Map<String, Variable<*>>

    override fun variables(): Collection<Variable<*>> {
        return variables.values
    }

    override fun variable(name: String): Variable<*>? {
        return variables[name]
    }

    override fun createVariant(
        base: ParameterStateProvider,
    ): Result<KernelVariantImpl> {
        val config = KernelVariantConfig(this, base)
        return generateVariant(config)
    }

    override fun createVariant(
        base: ParameterStateProvider,
        lambda: KernelVariantBuilder.() -> Unit,
    ): Result<KernelVariantImpl> {
        val config = KernelVariantBuilderImpl(this, base).apply(lambda).build()
        return generateVariant(config)
    }

    override fun createVariant(
        values: Map<String, Any?>,
        base: ParameterStateProvider,
    ): Result<KernelVariantImpl> {
        val config = KernelVariantBuilderImpl(this, base).apply(values).build()
        return generateVariant(config)
    }

    abstract fun generateVariant(config: KernelVariantConfig): Result<KernelVariantImpl>

    abstract fun generateInstance(config: KernelInstanceConfig): Result<KernelInstanceImpl>

    abstract fun updateKernel(state: MutableKernelState)

    override fun generateDefaultParameterStates(): KernelVariantConfig {
        val parameterStates = mutableMapOf<String, Any?>()
        for (parameter in parameters()) {
            parameterStates[parameter.name] = parameter.initialization()
        }
        return KernelVariantConfig(this, parameterStates)
    }
}
