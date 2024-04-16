package net.voxelpi.vire.api.simulation

import net.voxelpi.event.EventScope
import net.voxelpi.vire.api.circuit.Circuit
import net.voxelpi.vire.api.environment.Environment

/**
 * The simulation that manages the state of all components and networks.
 */
interface Simulation {

    /**
     * The environment of the simulation.
     */
    val environment: Environment

    /**
     * The event scope of the simulation.
     */
    val eventScope: EventScope

    /**
     * The circuit of the simulation.
     */
    val circuit: Circuit

    /**
     * Simulates [numberOfSteps] steps.
     */
    fun simulateSteps(numberOfSteps: Int)
}
