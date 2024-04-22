package net.voxelpi.vire.engine.circuit.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.kernel.variable.Field
import net.voxelpi.vire.engine.circuit.kernel.variable.Input
import net.voxelpi.vire.engine.circuit.kernel.variable.Output
import net.voxelpi.vire.engine.circuit.kernel.variable.Parameter

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
     * The parameters pf the kernel.
     */
    public val parameters: Map<String, Parameter<*>>

    /**
     * The fields pf the kernel.
     */
    public val fields: Map<String, Field<*>>

    /**
     * The inputs pf the kernel.
     */
    public val inputs: Map<String, Input>

    /**
     * The Outputs pf the kernel.
     */
    public val outputs: Map<String, Output>

    /**
     * Returns all parameters that are registered on the kernel.
     */
    public fun parameters(): Collection<Parameter<*>> = parameters.values

    /**
     * Returns all fields that are registered on the kernel.
     */
    public fun fields(): Collection<Field<*>> = fields.values

    /**
     * Returns all inputs that are registered on the kernel.
     */
    public fun inputs(): Collection<Input> = inputs.values

    /**
     * Returns all outputs that are registered on the kernel.
     */
    public fun outputs(): Collection<Output> = outputs.values
}

internal interface KernelImpl : Kernel {

    /**
     * Process the given [configuration] and generate [KernelConfigurationResults].
     */
    fun processConfiguration(configuration: KernelConfiguration): Result<KernelConfigurationResults>

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
        for (parameter in parameters.values) {
            parameterStates[parameter.name] = parameter.initialization.provideValue()
        }
        return parameterStates
    }

    /**
     * Returns a map that contains all io vectors of the kernel and their default sizes.
     */
    fun generateDefaultIOVectorSizes(): MutableMap<String, Int> {
        val ioVectorSizes = mutableMapOf<String, Int>()
        for (input in inputs.values) {
            ioVectorSizes[input.name] = input.initialSize.provideValue()
        }
        return ioVectorSizes
    }
}
