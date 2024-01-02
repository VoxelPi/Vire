package net.voxelpi.vire.engine.simulation.network

import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.network.Network
import net.voxelpi.vire.api.simulation.network.NetworkNode
import net.voxelpi.vire.engine.simulation.VireSimulation
import net.voxelpi.vire.engine.simulation.VireSimulationObject
import net.voxelpi.vire.engine.simulation.component.VireComponentPort
import java.util.UUID

class VireNetwork(
    override val simulation: VireSimulation,
    override val uniqueId: UUID = UUID.randomUUID(),
    override var state: LogicState = LogicState.NONE,
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

    override fun nodes(): List<VireNetworkNode> {
        return nodes.values.toList()
    }

    override fun createNode(connectedTo: Collection<NetworkNode>, uniqueId: UUID): VireNetworkNode {
        // Check that the node is connected.
        if (nodes.isNotEmpty()) {
            require(connectedTo.isNotEmpty()) { "Created node must be connected to at least one existing node." }
        }

        // Check that all node that the node is connected to are in the network.
        for (connectedNode in connectedTo) {
            require(connectedNode is VireNetworkNode)
            require(connectedNode.network == this) { "Connected node is in different network" }
        }

        // Create the node.
        val node = simulation.createNetworkNode(this, uniqueId)

        // Register a connection to every connected node in the created node.
        for (connectedNode in connectedTo) {
            require(connectedNode is VireNetworkNode)
            node.registerConnection(connectedNode)
            connectedNode.registerConnection(node)
        }

        // Return the created node.
        return node
    }

    override fun removeNode(node: NetworkNode) {
        simulation.removeNetworkNode(node)
    }

    override fun ports(): Collection<VireComponentPort> {
        return nodes.values.mapNotNull { it.holder }.filterIsInstance<VireComponentPort>()
    }

    override fun pushPortOutputs(): LogicState {
        state = LogicState.NONE

        // Push the last simulated state onto the network.
        for (port in ports()) {
            port.pushOutput()
        }

        return state
    }

    override fun remove() {
        simulation.removeNetwork(this)
    }

    fun destroy() {
        nodes.clear()
    }
}
