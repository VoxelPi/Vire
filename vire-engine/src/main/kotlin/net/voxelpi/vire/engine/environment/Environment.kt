package net.voxelpi.vire.engine.environment

import net.voxelpi.vire.engine.circuit.Circuit

/**
 * A vire environment.
 */
public interface Environment {

    /**
     * Creates a new circuit.
     */
    public fun createCircuit(): Circuit
}
