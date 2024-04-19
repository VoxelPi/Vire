package net.voxelpi.vire.engine.circuit.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.kernel.variable.Variable

public interface Kernel {

    /**
     * The id of the kernel.
     */
    public val id: Identifier

    public val tags: Set<Identifier>

    public val properties: Map<Identifier, String>

    public val variables: Map<String, Variable>
}
