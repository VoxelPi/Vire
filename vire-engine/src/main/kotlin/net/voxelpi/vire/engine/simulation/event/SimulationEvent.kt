package net.voxelpi.vire.engine.simulation.event

import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.environment.event.EnvironmentEvent
import net.voxelpi.vire.engine.simulation.Simulation

/**
 * An event that is bound to a simulation.
 */
public interface SimulationEvent : EnvironmentEvent {

    /**
     * The affected simulation.
     */
    public val simulation: Simulation

    override val environment: Environment
        get() = simulation.environment
}
