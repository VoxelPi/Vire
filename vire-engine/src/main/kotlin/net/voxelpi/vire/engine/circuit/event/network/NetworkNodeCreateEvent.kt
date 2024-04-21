package net.voxelpi.vire.engine.circuit.event.network

import net.voxelpi.vire.engine.circuit.network.NetworkNode

/**
 * An event that is called when a network node is created.
 */
public data class NetworkNodeCreateEvent(
    override val node: NetworkNode,
) : NetworkNodeEvent
