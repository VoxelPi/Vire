package net.voxelpi.vire.api.circuit.event.network

import net.voxelpi.vire.api.circuit.network.Network

/**
 * An event that is called when a network is destroyed.
 */
class NetworkDestroyEvent(
    override val network: Network,
) : NetworkEvent
