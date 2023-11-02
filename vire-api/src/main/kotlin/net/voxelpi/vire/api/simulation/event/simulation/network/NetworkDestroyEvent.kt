package net.voxelpi.vire.api.simulation.event.simulation.network

import net.voxelpi.vire.api.simulation.network.Network

/**
 * An event that is called when a network is destroyed.
 */
class NetworkDestroyEvent(
    override val network: Network,
) : NetworkEvent
