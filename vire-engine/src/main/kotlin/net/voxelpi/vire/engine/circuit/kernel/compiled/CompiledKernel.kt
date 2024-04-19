package net.voxelpi.vire.engine.circuit.kernel.compiled

import net.voxelpi.vire.engine.circuit.kernel.Kernel

public interface CompiledKernel : Kernel {

    /**
     * The configuration action of the state machine.
     */
    public val configure: (ConfigurationContext) -> Unit

    /**
     * The update action fo the state machine.
     */
    public val update: (UpdateContext) -> Unit
}
