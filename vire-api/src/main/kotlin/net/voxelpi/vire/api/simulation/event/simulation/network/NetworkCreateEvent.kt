package net.voxelpi.vire.api.simulation.event.simulation.network

import net.voxelpi.vire.api.simulation.network.Network

/**
 * An event that is called when a network is created.
 */
data class NetworkCreateEvent(
    override val network: Network,
) : NetworkEvent
