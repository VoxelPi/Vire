package net.voxelpi.vire.engine.circuit.component

import net.voxelpi.vire.engine.circuit.component.port.ComponentPort
import net.voxelpi.vire.engine.circuit.kernel.Kernel
import net.voxelpi.vire.engine.circuit.kernel.KernelInstance

public interface Component {

    /**
     * The kernel of the component.
     */
    public val kernel: Kernel

    /**
     * The kernel instance of the component.
     */
    public val kernelInstance: KernelInstance

    /**
     * Returns all ports of this component.
     */
    public fun ports(): Collection<ComponentPort>
}
