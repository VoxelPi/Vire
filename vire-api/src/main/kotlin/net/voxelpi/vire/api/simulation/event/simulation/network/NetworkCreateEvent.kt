package net.voxelpi.vire.api.simulation.event.simulation.network

import net.voxelpi.vire.api.simulation.network.Network

data class NetworkCreateEvent(
    override val network: Network,
) : NetworkEvent
