package net.voxelpi.vire.engine.circuit.event.network

import net.voxelpi.vire.engine.circuit.network.NetworkConnection

/**
 * An event that is posted, when a new network connection is created.
 */
public data class NetworkConnectionCreateEvent(
    override val connection: NetworkConnection,
) : NetworkConnectionEvent
