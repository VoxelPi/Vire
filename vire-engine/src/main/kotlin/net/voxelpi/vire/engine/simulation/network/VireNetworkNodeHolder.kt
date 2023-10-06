package net.voxelpi.vire.engine.simulation.network

import net.voxelpi.vire.api.simulation.network.NetworkNodeHolder

interface VireNetworkNodeHolder : NetworkNodeHolder {

    override val node: VireNetworkNode
}
