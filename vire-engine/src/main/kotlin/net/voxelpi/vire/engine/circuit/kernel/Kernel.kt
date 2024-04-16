package net.voxelpi.vire.engine.circuit.kernel

import net.voxelpi.vire.engine.circuit.kernel.variable.Variable

public interface Kernel {

    public val variables: Map<String, Variable>
}
