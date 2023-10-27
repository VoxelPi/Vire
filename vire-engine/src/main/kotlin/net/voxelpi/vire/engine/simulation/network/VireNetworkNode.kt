package net.voxelpi.vire.engine.simulation.network

import net.voxelpi.vire.api.simulation.network.NetworkNode
import net.voxelpi.vire.engine.simulation.VireSimulation
import net.voxelpi.vire.engine.simulation.VireSimulationObject
import java.util.UUID

class VireNetworkNode(
    override val simulation: VireSimulation,
    network: VireNetwork = simulation.createNetwork(),
    override val uniqueId: UUID = UUID.randomUUID(),
    var holder: VireNetworkNodeHolder? = null,
) : VireSimulationObject(), NetworkNode {

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
        return connectedNodes.mapNotNull(simulation::networkNode)
    }

    override fun isDirectlyConnectedTo(node: NetworkNode): Boolean {
        return node.network == this.network && connectedNodes.contains(node.uniqueId)
    }

    override fun isConnectedTo(node: NetworkNode): Boolean {
        return simulation.areNodesConnected(this, node)
    }

    override fun remove() {
        simulation.removeNetworkNode(this)
    }
}
