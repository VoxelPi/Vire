package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.FieldProvider
import net.voxelpi.vire.engine.kernel.variable.Input
import net.voxelpi.vire.engine.kernel.variable.InputProvider
import net.voxelpi.vire.engine.kernel.variable.Output
import net.voxelpi.vire.engine.kernel.variable.OutputProvider
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.ParameterProvider
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.SettingProvider
import net.voxelpi.vire.engine.kernel.variable.Variable

/**
 * A kernel is logical processor that produces logical outputs from its inputs and other parameters.
 */
public interface Kernel : ParameterProvider, SettingProvider, FieldProvider, InputProvider, OutputProvider {

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
     */
    public fun createVariant(): KernelVariant

    /**
     * Creates a new variant of the kernel using the given [lambda] to initialize the parameters.
     * Before the builder is run, all parameters of the kernel are initialized to their default values,
     * therefore the builder doesn't have to set every parameter.
     *
     * @param lambda the receiver lambda which will be invoked on the builder.
     */
    public fun createVariant(lambda: KernelVariantBuilder.() -> Unit): KernelVariant

    /**
     * Creates a new variant of the kernel using the given [values] as the state of the parameters.
     * The value map doesn't have to contain entries for every parameter,
     * parameters without specified value are set to their default value.
     * However, the value map must not have any entries for parameters that do not belong to the kernel.
     *
     * @param values the values that should be applied to the kernel configuration.
     */
    public fun createVariant(values: Map<String, Any?>): KernelVariant
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

    override fun parameters(): Collection<Parameter<*>> {
        return variables.values.filterIsInstance<Parameter<*>>()
    }

    override fun parameter(name: String): Parameter<*>? {
        return variableOfKind<Parameter<*>>(name)
    }

    override fun settings(): Collection<Setting<*>> {
        return variables.values.filterIsInstance<Setting<*>>()
    }

    override fun setting(name: String): Setting<*>? {
        return variableOfKind<Setting<*>>(name)
    }

    override fun fields(): Collection<Field<*>> {
        return variables.values.filterIsInstance<Field<*>>()
    }

    override fun field(name: String): Field<*>? {
        return variableOfKind<Field<*>>(name)
    }

    override fun inputs(): Collection<Input> {
        return variables.values.filterIsInstance<Input>()
    }

    override fun input(name: String): Input? {
        return variableOfKind<Input>(name)
    }

    override fun outputs(): Collection<Output> {
        return variables.values.filterIsInstance<Output>()
    }

    override fun output(name: String): Output? {
        return variableOfKind<Output>(name)
    }

    private inline fun <reified T : Variable<*>> variableOfKind(name: String): T? {
        val variable = variables[name] ?: return null
        return if (variable is T) variable else null
    }

    override fun createVariant(): KernelVariantImpl {
        val builder = KernelVariantBuilderImpl(this, generateDefaultParameterStates())
        val variant = KernelVariantImpl(this, builder)
        return variant
    }

    override fun createVariant(lambda: KernelVariantBuilder.() -> Unit): KernelVariantImpl {
        val builder = KernelVariantBuilderImpl(this, generateDefaultParameterStates())
            .apply(lambda)
        return KernelVariantImpl(this, builder)
    }

    override fun createVariant(values: Map<String, Any?>): KernelVariantImpl {
        val builder = KernelVariantBuilderImpl(this, generateDefaultParameterStates())
        for ((parameterName, parameterValue) in values) {
            // Check that only existing parameters are specified.
            val parameter = parameter(parameterName)
                ?: throw IllegalArgumentException("Unknown parameter '$parameterName'")

            // Check that the value is valid for the parameter.
            require(parameter.isValidValue(parameterValue)) { "Invalid value for the parameter ${parameter.name}" }
            builder[parameterName] = parameterValue
        }
        return KernelVariantImpl(this, builder)
    }

    /**
     * Process the given [builder] and generate [KernelVariantData].
     */
    abstract fun configureKernel(builder: KernelVariantBuilder): Result<KernelVariantData>

    abstract fun initializeKernel(state: KernelInstance)

    abstract fun updateKernel(state: KernelInstance)

    /**
     * Returns a map that contains all parameters of the kernel and their default values.
     */
    fun generateDefaultConfiguration(): KernelVariantBuilderImpl {
        return KernelVariantBuilderImpl(this, generateDefaultParameterStates())
    }

    /**
     * Returns a map that contains all parameters of the kernel and their default values.
     */
    fun generateDefaultParameterStates(): MutableMap<String, Any?> {
        val parameterStates = mutableMapOf<String, Any?>()
        for (parameter in parameters()) {
            parameterStates[parameter.name] = parameter.initialization.provideValue()
        }
        return parameterStates
    }

    /**
     * Returns a map that contains all io vectors of the kernel and their default sizes.
     */
    fun generateDefaultIOVectorSizes(): MutableMap<String, Int> {
        val ioVectorSizes = mutableMapOf<String, Int>()
        for (input in inputs()) {
            ioVectorSizes[input.name] = input.initialSize.provideValue()
        }
        for (output in outputs()) {
            ioVectorSizes[output.name] = output.initialSize.provideValue()
        }
        return ioVectorSizes
    }
}
