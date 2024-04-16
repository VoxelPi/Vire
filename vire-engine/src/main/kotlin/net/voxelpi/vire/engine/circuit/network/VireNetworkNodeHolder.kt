package net.voxelpi.vire.engine.circuit.network

import net.voxelpi.vire.api.circuit.network.NetworkNodeHolder

interface VireNetworkNodeHolder : NetworkNodeHolder {

    override val node: VireNetworkNode
}
