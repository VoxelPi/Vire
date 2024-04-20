package net.voxelpi.vire.engine.circuit.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.kernel.variable.Field
import net.voxelpi.vire.engine.circuit.kernel.variable.Input
import net.voxelpi.vire.engine.circuit.kernel.variable.Output
import net.voxelpi.vire.engine.circuit.kernel.variable.Parameter

public interface Kernel {

    /**
     * The id of the kernel.
     */
    public val id: Identifier

    public val tags: Set<Identifier>

    public val properties: Map<Identifier, String>

    public val parameters: Map<String, Parameter<*>>

    public val fields: Map<String, Field<*>>

    public val inputs: Map<String, Input>

    public val outputs: Map<String, Output>

    public fun parameters(): Collection<Parameter<*>> = parameters.values

    public fun fields(): Collection<Field<*>> = fields.values

    public fun inputs(): Collection<Input> = inputs.values

    public fun outputs(): Collection<Output> = outputs.values
}

internal interface KernelImpl : Kernel
