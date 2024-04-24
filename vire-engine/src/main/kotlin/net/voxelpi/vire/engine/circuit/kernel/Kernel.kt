package net.voxelpi.vire.engine.circuit.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.kernel.variable.Field
import net.voxelpi.vire.engine.circuit.kernel.variable.FieldProvider
import net.voxelpi.vire.engine.circuit.kernel.variable.Input
import net.voxelpi.vire.engine.circuit.kernel.variable.InputProvider
import net.voxelpi.vire.engine.circuit.kernel.variable.Output
import net.voxelpi.vire.engine.circuit.kernel.variable.OutputProvider
import net.voxelpi.vire.engine.circuit.kernel.variable.Parameter
import net.voxelpi.vire.engine.circuit.kernel.variable.ParameterProvider
import net.voxelpi.vire.engine.circuit.kernel.variable.Setting
import net.voxelpi.vire.engine.circuit.kernel.variable.SettingProvider
import net.voxelpi.vire.engine.circuit.kernel.variable.Variable

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

    /**
     * Process the given [configuration] and generate [KernelConfigurationResults].
     */
    abstract fun configureKernel(configuration: KernelConfiguration): Result<KernelConfigurationResults>

    abstract fun initializeKernel(state: KernelInstance)

    abstract fun updateKernel(state: KernelInstance)

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
