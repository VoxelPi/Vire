package net.voxelpi.vire.engine.circuit.network

import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.circuit.network.Network
import net.voxelpi.vire.api.circuit.network.NetworkNode
import net.voxelpi.vire.engine.circuit.VireCircuit
import net.voxelpi.vire.engine.circuit.VireCircuitElement
import net.voxelpi.vire.engine.circuit.component.VireComponentPort
import java.util.UUID

class VireNetwork(
    override val circuit: VireCircuit,
    override val uniqueId: UUID = UUID.randomUUID(),
    override var state: LogicState = LogicState.EMPTY,
) : VireCircuitElement(), Network {

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
        val node = circuit.createNetworkNode(this, uniqueId)

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
        circuit.removeNetworkNode(node)
    }

    override fun ports(): Collection<VireComponentPort> {
        return nodes.values.mapNotNull { it.holder }.filterIsInstance<VireComponentPort>()
    }

    override fun pushPortOutputs(): LogicState {
        state = LogicState.EMPTY

        // Push the last simulated state onto the network.
        for (port in ports()) {
            port.pushOutput()
        }

        return state
    }

    override fun remove() {
        circuit.removeNetwork(this)
    }

    fun destroy() {
        nodes.clear()
    }
}
