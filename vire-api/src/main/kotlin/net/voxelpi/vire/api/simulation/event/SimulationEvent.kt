package net.voxelpi.vire.api.simulation.event

import net.voxelpi.vire.api.simulation.Simulation

/**
 * An event in the simulation.
 */
interface SimulationEvent {

    /**
     * The simulation instance.
     */
    val simulation: Simulation
}
