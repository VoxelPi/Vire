package net.voxelpi.vire.engine.circuit.kernel.circuit

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.kernel.Kernel
import net.voxelpi.vire.engine.circuit.kernel.KernelConfiguration
import net.voxelpi.vire.engine.circuit.kernel.KernelConfigurationResults
import net.voxelpi.vire.engine.circuit.kernel.KernelImpl
import net.voxelpi.vire.engine.circuit.kernel.KernelState

public interface CircuitKernel : Kernel {

    public val circuit: Circuit
}

internal class CircuitKernelImpl(
    id: Identifier,
    override val circuit: Circuit,
) : KernelImpl(id), CircuitKernel {

    override fun configureKernel(configuration: KernelConfiguration): Result<KernelConfigurationResults> {
        TODO("Not yet implemented")
    }

    override fun initializeKernel(state: KernelState) {
        TODO("Not yet implemented")
    }

    override fun updateKernel(state: KernelState) {
        TODO("Not yet implemented")
    }
}
