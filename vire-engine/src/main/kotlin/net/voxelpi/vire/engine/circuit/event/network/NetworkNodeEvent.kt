package net.voxelpi.vire.engine.circuit.event.network

import net.voxelpi.vire.engine.circuit.network.Network
import net.voxelpi.vire.engine.circuit.network.NetworkNode

/**
 * An event that affect a network node port.
 */
public interface NetworkNodeEvent : NetworkEvent {

    /**
     * The affected network node.
     */
    public val node: NetworkNode

    override val network: Network
        get() = node.network
}
