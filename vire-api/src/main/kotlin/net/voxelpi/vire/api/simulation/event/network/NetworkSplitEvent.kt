package net.voxelpi.vire.api.simulation.event.network

import net.voxelpi.vire.api.simulation.network.Network

/**
 * An event that is called when networks are merged.
 * @property network the network that is being split.
 * @property splitNetworks the resulting networks.
 */
data class NetworkSplitEvent(
    override val network: Network,
    val splitNetworks: List<Network>,
) : NetworkEvent