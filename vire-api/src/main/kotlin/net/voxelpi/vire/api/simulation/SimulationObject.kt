package net.voxelpi.vire.api.simulation

import java.util.UUID

interface SimulationObject {

    /**
     * The simulation to which the simulation object belongs to.
     */
    val simulation: Simulation

    /**
     * The unique id of the simulation object.
     */
    val uniqueId: UUID
}
