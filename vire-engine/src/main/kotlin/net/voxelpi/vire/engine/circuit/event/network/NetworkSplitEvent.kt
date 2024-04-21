package net.voxelpi.vire.engine.circuit.event.network

import net.voxelpi.vire.engine.circuit.network.Network

/**
 * An event that is called when networks are merged.
 * @property network the network that is being split.
 * @property splitNetworks the resulting networks.
 */
public data class NetworkSplitEvent(
    override val network: Network,
    public val splitNetworks: List<Network>,
) : NetworkEvent
