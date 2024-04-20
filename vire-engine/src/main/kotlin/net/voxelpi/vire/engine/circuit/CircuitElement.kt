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

    /**
     * Removes the element from the circuit.
     */
    public fun remove()
}

internal abstract class CircuitElementImpl : CircuitElement {

    abstract override val circuit: CircuitImpl

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CircuitElementImpl

        return uniqueId == other.uniqueId
    }

    override fun hashCode(): Int {
        return uniqueId.hashCode()
    }
}
