package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.environment.Environment

/**
 * A simulation of a logic circuit.
 */
public interface Simulation {

    /**
     * The environment of the simulation.
     */
    public val environment: Environment

    /**
     * The simulated circuit.
     */
    public val circuit: Circuit
}
