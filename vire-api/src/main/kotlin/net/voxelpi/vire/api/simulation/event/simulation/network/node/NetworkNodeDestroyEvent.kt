package net.voxelpi.vire.api.simulation.event.simulation.network.node

import net.voxelpi.vire.api.simulation.network.NetworkNode

data class NetworkNodeDestroyEvent(
    override val node: NetworkNode,
) : NetworkNodeEvent
