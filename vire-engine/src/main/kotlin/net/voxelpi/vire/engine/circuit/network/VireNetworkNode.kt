package net.voxelpi.vire.engine.circuit.network

import net.voxelpi.vire.api.circuit.network.NetworkNode
import net.voxelpi.vire.engine.circuit.VireCircuit
import net.voxelpi.vire.engine.circuit.VireCircuitElement
import java.util.UUID

class VireNetworkNode(
    override val circuit: VireCircuit,
    network: VireNetwork = circuit.createNetwork(),
    override val uniqueId: UUID = UUID.randomUUID(),
    var holder: VireNetworkNodeHolder? = null,
) : VireCircuitElement(), NetworkNode {

    init {
        network.registerNode(this)
    }

    override var network: VireNetwork = network
        set(value) {
            field.unregisterNode(this)
            field = value
            field.registerNode(this)
        }

    protected val connectedNodes: MutableSet<UUID> = mutableSetOf()

    fun registerConnection(node: VireNetworkNode) {
        connectedNodes.add(node.uniqueId)
    }

    fun unregisterConnection(node: VireNetworkNode) {
        connectedNodes.remove(node.uniqueId)
    }

    override fun connectedNodes(): Collection<VireNetworkNode> {
        return connectedNodes.mapNotNull(circuit::networkNode)
    }

    override fun isDirectlyConnectedTo(node: NetworkNode): Boolean {
        return node.network == this.network && connectedNodes.contains(node.uniqueId)
    }

    override fun isConnectedTo(node: NetworkNode): Boolean {
        return circuit.areNodesConnected(this, node)
    }

    override fun remove() {
        network.removeNode(this)
    }
}
