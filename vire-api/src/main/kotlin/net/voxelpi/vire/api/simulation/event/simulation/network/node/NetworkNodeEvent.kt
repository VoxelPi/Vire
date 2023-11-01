package net.voxelpi.vire.api.simulation.event.simulation.network.node

import net.voxelpi.vire.api.simulation.event.simulation.network.NetworkEvent
import net.voxelpi.vire.api.simulation.network.Network
import net.voxelpi.vire.api.simulation.network.NetworkNode

interface NetworkNodeEvent : NetworkEvent {

    val node: NetworkNode

    override val network: Network
        get() = node.network
}
