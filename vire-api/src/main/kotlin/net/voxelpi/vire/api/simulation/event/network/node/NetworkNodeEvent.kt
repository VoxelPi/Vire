package net.voxelpi.vire.api.simulation.event.network.node

import net.voxelpi.vire.api.simulation.event.network.NetworkEvent
import net.voxelpi.vire.api.simulation.network.Network
import net.voxelpi.vire.api.simulation.network.NetworkNode

/**
 * An event that affect a network node port.
 */
interface NetworkNodeEvent : NetworkEvent {

    /**
     * The affected network node.
     */
    val node: NetworkNode

    override val network: Network
        get() = node.network
}
