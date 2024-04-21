package net.voxelpi.vire.engine.circuit.event.network

import net.voxelpi.vire.engine.circuit.network.Network
import net.voxelpi.vire.engine.circuit.network.NetworkConnection

/**
 * An event that affects a network connection.
 */
public interface NetworkConnectionEvent : NetworkEvent {

    /**
     * The affected network connection.
     */
    public val connection: NetworkConnection

    override val network: Network
        get() = connection.network
}
