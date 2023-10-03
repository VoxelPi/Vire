package net.voxelpi.vire.simulation.network

import net.voxelpi.vire.api.simulation.network.NetworkNode
import net.voxelpi.vire.simulation.VireSimulation
import net.voxelpi.vire.simulation.VireSimulationObject
import java.util.UUID

class VireNetworkNode(
    override val simulation: VireSimulation,
    override var network: VireNetwork = simulation.createNetwork(),
    override val uniqueId: UUID = UUID.randomUUID()
) : VireSimulationObject(), NetworkNode {

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
