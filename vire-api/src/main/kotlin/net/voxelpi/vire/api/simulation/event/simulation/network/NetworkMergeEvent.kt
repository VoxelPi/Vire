package net.voxelpi.vire.api.simulation.event.simulation.network

import net.voxelpi.vire.api.simulation.network.Network

/**
 * An event that is called when networks are merged.
 * @property network the resulting network.
 * @property mergedNetworks the networks that are being merged.
 */
data class NetworkMergeEvent(
    override val network: Network,
    val mergedNetworks: List<Network>,
) : NetworkEvent
