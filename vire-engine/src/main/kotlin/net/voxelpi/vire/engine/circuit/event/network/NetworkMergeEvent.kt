package net.voxelpi.vire.engine.circuit.event.network

import net.voxelpi.vire.engine.circuit.network.Network

/**
 * An event that is called when networks are merged.
 * @property network the resulting network.
 * @property mergedNetworks the networks that are being merged.
 */
public data class NetworkMergeEvent(
    override val network: Network,
    public val mergedNetworks: List<Network>,
) : NetworkEvent
