package net.voxelpi.vire.engine.circuit.event.network

import net.voxelpi.vire.engine.circuit.network.NetworkNode

/**
 * An event that is called when a network node is destroyed.
 */
public data class NetworkNodeDestroyEvent(
    override val node: NetworkNode,
) : NetworkNodeEvent
