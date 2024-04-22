package net.voxelpi.vire.engine.circuit.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.kernel.variable.Field
import net.voxelpi.vire.engine.circuit.kernel.variable.Input
import net.voxelpi.vire.engine.circuit.kernel.variable.Output
import net.voxelpi.vire.engine.circuit.kernel.variable.Parameter
import net.voxelpi.vire.engine.circuit.kernel.variable.Setting
import net.voxelpi.vire.engine.circuit.kernel.variable.Variable

/**
 * A kernel is logical processor that produces logical outputs from its inputs and other parameters.
 */
public interface Kernel {

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
     * Returns all registered variables.
     */
    public fun variables(): Collection<Variable<*>>

    /**
     * Returns the variable with the given [name].
     */
    public fun variable(name: String): Variable<*>?

    /**
     * Checks if the kernel has a variable with the given [name].
     */
    public fun hasVariable(name: String): Boolean = variable(name) != null

    /**
     * Returns all parameters that are registered on the kernel.
     */
    public fun parameters(): Collection<Parameter<*>>

    /**
     * Returns the parameter with the given [name].
     */
    public fun parameter(name: String): Parameter<*>?

    /**
     * Checks if the kernel has a parameter with the given [name].
     */
    public fun hasParameter(name: String): Boolean = parameter(name) != null

    /**
     * Returns all settings that are registered on the kernel.
     */
    public fun settings(): Collection<Setting<*>>

    /**
     * Returns the setting with the given [name].
     */
    public fun setting(name: String): Setting<*>?

    /**
     * Checks if the kernel has a setting with the given [name].
     */
    public fun hasSetting(name: String): Boolean = setting(name) != null

    /**
     * Returns all fields that are registered on the kernel.
     */
    public fun fields(): Collection<Field<*>>

    /**
     * Returns the field with the given [name].
     */
    public fun field(name: String): Field<*>?

    /**
     * Checks if the kernel has a field with the given [name].
     */
    public fun hasField(name: String): Boolean = field(name) != null

    /**
     * Returns all inputs that are registered on the kernel.
     */
    public fun inputs(): Collection<Input>

    /**
     * Returns the input with the given [name].
     */
    public fun input(name: String): Input?

    /**
     * Checks if the kernel has an input with the given [name].
     */
    public fun hasInput(name: String): Boolean = input(name) != null

    /**
     * Returns all outputs that are registered on the kernel.
     */
    public fun outputs(): Collection<Output>

    /**
     * Returns the output with the given [name].
     */
    public fun output(name: String): Output?

    /**
     * Checks if the kernel has an output with the given [name].
     */
    public fun hasOutput(name: String): Boolean = output(name) != null
}

internal abstract class KernelImpl(
    override val id: Identifier,
) : Kernel {

    override val tags: MutableSet<Identifier> = mutableSetOf()
    override val properties: MutableMap<Identifier, String> = mutableMapOf()
    protected val variables: MutableMap<String, Variable<*>> = mutableMapOf()

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
        return variableWithSpecies<Parameter<*>>(name)
    }

    override fun settings(): Collection<Setting<*>> {
        return variables.values.filterIsInstance<Setting<*>>()
    }

    override fun setting(name: String): Setting<*>? {
        return variableWithSpecies<Setting<*>>(name)
    }

    override fun fields(): Collection<Field<*>> {
        return variables.values.filterIsInstance<Field<*>>()
    }

    override fun field(name: String): Field<*>? {
        return variableWithSpecies<Field<*>>(name)
    }

    override fun inputs(): Collection<Input> {
        return variables.values.filterIsInstance<Input>()
    }

    override fun input(name: String): Input? {
        return variableWithSpecies<Input>(name)
    }

    override fun outputs(): Collection<Output> {
        return variables.values.filterIsInstance<Output>()
    }

    override fun output(name: String): Output? {
        return variableWithSpecies<Output>(name)
    }

    private inline fun <reified T : Variable<*>> variableWithSpecies(name: String): T? {
        val variable = variables[name] ?: return null
        return if (variable is T) variable else null
    }

    /**
     * Process the given [configuration] and generate [KernelConfigurationResults].
     */
    abstract fun configureKernel(configuration: KernelConfiguration): Result<KernelConfigurationResults>

    abstract fun initializeKernel(state: KernelState)

    abstract fun updateKernel(state: KernelState)

    /**
     * Returns a map that contains all parameters of the kernel and their default values.
     */
    fun generateDefaultConfiguration(): KernelConfigurationImpl {
        return KernelConfigurationImpl(this, generateDefaultParameterStates())
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
