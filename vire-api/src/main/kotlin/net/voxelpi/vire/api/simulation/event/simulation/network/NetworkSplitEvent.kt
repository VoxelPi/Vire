package net.voxelpi.vire.api.simulation.event.simulation.network

import net.voxelpi.vire.api.simulation.network.Network

data class NetworkSplitEvent(
    override val network: Network,
    val splitNetworks: List<Network>,
) : NetworkEvent
