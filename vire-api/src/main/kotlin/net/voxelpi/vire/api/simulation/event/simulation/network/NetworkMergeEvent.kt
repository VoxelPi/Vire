package net.voxelpi.vire.api.simulation.event.simulation.network

import net.voxelpi.vire.api.simulation.network.Network

data class NetworkMergeEvent(
    override val network: Network,
    val mergedNetworks: List<Network>,
) : NetworkEvent
