package net.voxelpi.vire.engine.circuit.event.network

import net.voxelpi.vire.engine.circuit.network.NetworkConnection

/**
 * An event that is posted, when a network connection is destroyed.
 */
public data class NetworkConnectionDestroyEvent(
    override val connection: NetworkConnection,
) : NetworkConnectionEvent
