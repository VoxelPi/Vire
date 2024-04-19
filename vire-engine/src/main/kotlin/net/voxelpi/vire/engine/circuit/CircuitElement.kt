package net.voxelpi.vire.engine.circuit

import java.util.UUID

/**
 * An element of a circuit.
 */
public interface CircuitElement {

    /**
     * The circuit to which the element belongs to.
     */
    public val circuit: Circuit

    /**
     * The unique id of the simulation object.
     */
    public val uniqueId: UUID
}
