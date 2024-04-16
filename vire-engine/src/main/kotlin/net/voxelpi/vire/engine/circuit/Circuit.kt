package net.voxelpi.vire.engine.circuit

import net.voxelpi.vire.engine.circuit.kernel.Kernel
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.simulation.Simulation

/**
 * A logic circuit created by linking different components together.
 */
public interface Circuit : Kernel {

    /**
     * The environment of the circuit.
     */
    public val environment: Environment

    /**
     * Creates a new simulation of this circuit.
     */
    public fun createSimulation(): Simulation
}
