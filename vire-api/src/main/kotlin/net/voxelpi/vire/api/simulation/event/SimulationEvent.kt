package net.voxelpi.vire.api.simulation.event

import net.voxelpi.vire.api.environment.Environment
import net.voxelpi.vire.api.environment.event.EnvironmentEvent
import net.voxelpi.vire.api.simulation.Simulation

/**
 * An event that is bound to a simulation.
 */
interface SimulationEvent : EnvironmentEvent {

    /**
     * The affected simulation.
     */
    val simulation: Simulation

    override val environment: Environment
        get() = simulation.environment
}
