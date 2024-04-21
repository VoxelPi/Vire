package net.voxelpi.vire.engine.circuit.event.network

import net.voxelpi.vire.engine.circuit.network.Network

/**
 * An event that is called when a network is destroyed.
 */
public data class NetworkDestroyEvent(
    override val network: Network,
) : NetworkEvent
