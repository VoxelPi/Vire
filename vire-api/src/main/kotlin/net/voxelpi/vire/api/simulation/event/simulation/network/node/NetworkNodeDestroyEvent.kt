package net.voxelpi.vire.api.simulation.event.simulation.network.node

import net.voxelpi.vire.api.simulation.network.NetworkNode

/**
 * An event that is called when a network node is destroyed.
 */
data class NetworkNodeDestroyEvent(
    override val node: NetworkNode,
) : NetworkNodeEvent
