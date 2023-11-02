package net.voxelpi.vire.api.simulation.event.simulation.network.node

import net.voxelpi.vire.api.simulation.network.NetworkNode

/**
 * An event that is called when a network node is created.
 */
data class NetworkNodeCreateEvent(
    override val node: NetworkNode,
) : NetworkNodeEvent
