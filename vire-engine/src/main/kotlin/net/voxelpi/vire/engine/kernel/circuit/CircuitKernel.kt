package net.voxelpi.vire.engine.kernel.circuit

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelImpl
import net.voxelpi.vire.engine.kernel.KernelInstance
import net.voxelpi.vire.engine.kernel.KernelVariantBuilder
import net.voxelpi.vire.engine.kernel.KernelVariantData
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.field

public interface CircuitKernel : Kernel {

    public val circuit: Circuit
}

internal class CircuitKernelImpl(
    id: Identifier,
    tags: Set<Identifier>,
    properties: Map<Identifier, String>,
    override val circuit: CircuitImpl,
) : KernelImpl(id, tags, properties), CircuitKernel {

    override val variables: Map<String, Variable<*>>

    init {
        val variables: MutableMap<String, Variable<*>> = circuit.variables().associateBy { it.name }.toMutableMap()
        variables[STATE_FIELD.name] = STATE_FIELD
        this.variables = variables
    }

    override fun generateVariantData(builder: KernelVariantBuilder): Result<KernelVariantData> {
        return Result.success(KernelVariantData(builder, emptyMap()))
    }

    override fun initializeKernel(state: KernelInstance) {
        TODO("Not yet implemented")
    }

    override fun updateKernel(state: KernelInstance) {
        TODO("Not yet implemented")
    }

    companion object {
        private val STATE_FIELD = field("state", CircuitKernelStateImpl())
    }
}
