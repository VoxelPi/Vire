package net.voxelpi.vire.engine.simulation.network

import net.voxelpi.vire.api.simulation.network.Network
import net.voxelpi.vire.api.simulation.network.NetworkNode
import net.voxelpi.vire.api.simulation.network.NetworkState
import net.voxelpi.vire.engine.simulation.VireSimulation
import net.voxelpi.vire.engine.simulation.VireSimulationObject
import net.voxelpi.vire.engine.simulation.component.VireComponentPort
import java.util.UUID

class VireNetwork(
    override val simulation: VireSimulation,
    override val uniqueId: UUID = UUID.randomUUID(),
    override var state: NetworkState = NetworkState.None,
) : VireSimulationObject(), Network {

    private val nodes: MutableMap<UUID, VireNetworkNode> = mutableMapOf()

    fun registerNode(node: VireNetworkNode) {
        nodes[node.uniqueId] = node
    }

    fun unregisterNode(node: VireNetworkNode) {
        nodes.remove(node.uniqueId)

        // Remove the network if no nodes remain in the network, which means that the network is unused.
        if (nodes.isEmpty()) {
            remove()
        }
    }

    override operator fun contains(node: NetworkNode): Boolean {
        return nodes.contains(node.uniqueId)
    }

    override fun nodes(): Collection<VireNetworkNode> {
        return nodes.values
    }

    override fun createNode(connectedTo: Collection<NetworkNode>, uniqueId: UUID): VireNetworkNode {
        // Check that the node is connected.
        if (nodes.isNotEmpty()) {
            require(connectedTo.isNotEmpty()) { "Created node must be connected to at least one existing node." }
        }

        // Create the node.
        val node = simulation.createNetworkNode(this, uniqueId)

        // Register a connection to every connected node in the created node.
        for (connectedNode in connectedTo) {
            require(connectedNode is VireNetworkNode)
            if (connectedNode.network.uniqueId != this.uniqueId) {
                simulation.unregisterNetworkNode(node)
                throw IllegalArgumentException("Connected node is in different network")
            }
            node.registerConnection(connectedNode)
        }

        // Register a connection to the created node in every connected node.
        for (connectedNode in connectedTo) {
            require(connectedNode is VireNetworkNode)
            connectedNode.registerConnection(node)
        }

        // Return the created node.
        return node
    }

    override fun ports(): Collection<VireComponentPort> {
        return nodes.values.mapNotNull { it.holder }.filterIsInstance<VireComponentPort>()
    }

    override fun pushPortOutputs(): NetworkState {
        state = NetworkState.None

        // Push the last simulated state onto the network.
        for (port in ports()) {
            port.pushOutput()
        }

        return state
    }

    override fun remove() {
        val nodes = nodes.values.toList()
        for (node in nodes) {
            if (node.holder != null) {
                node.network = simulation.createNetwork(state = state)
                node.network.pushPortOutputs()
            } else {
                simulation.unregisterNetworkNode(node)
            }
        }

        simulation.unregisterNetwork(this)
    }
}
