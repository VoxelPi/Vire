package net.voxelpi.vire.api.simulation

import java.util.UUID

/**
 * An object of a simulation.
 */
interface CircuitElement {

    /**
     * The circuit to which the element belongs to.
     */
    val circuit: Circuit

    /**
     * The unique id of the simulation object.
     */
    val uniqueId: UUID

    /**
     * Removes the object from the simulation.
     */
    fun remove()
}
