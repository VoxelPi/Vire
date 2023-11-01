package net.voxelpi.vire.api.simulation.event.simulation.network

import net.voxelpi.vire.api.simulation.network.Network

class NetworkDestroyEvent(
    override val network: Network,
) : NetworkEvent
