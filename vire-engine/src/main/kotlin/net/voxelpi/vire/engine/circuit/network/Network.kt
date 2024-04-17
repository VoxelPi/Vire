package net.voxelpi.vire.engine.circuit.network

import net.voxelpi.vire.engine.LogicState

/**
 * A network of the logic circuit.
 */
public interface Network {

    /**
     * The initial state of the network when a new simulation is initialized.
     */
    public val initialState: LogicState
}
