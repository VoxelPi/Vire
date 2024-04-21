package net.voxelpi.vire.engine.circuit.event.network

import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.event.CircuitEvent
import net.voxelpi.vire.engine.circuit.network.Network

/**
 * An event that affect a network.
 */
public interface NetworkEvent : CircuitEvent {

    /**
     * The affected network.
     */
    public val network: Network

    override val circuit: Circuit
        get() = network.circuit
}
