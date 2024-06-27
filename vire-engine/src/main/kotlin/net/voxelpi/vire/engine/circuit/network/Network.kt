package net.voxelpi.vire.engine.circuit.network

import net.voxelpi.vire.engine.circuit.CircuitElement
import net.voxelpi.vire.engine.circuit.CircuitElementImpl
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.circuit.component.ComponentPort
import net.voxelpi.vire.engine.circuit.component.ComponentPortImpl
import net.voxelpi.vire.engine.circuit.terminal.Terminal
import net.voxelpi.vire.engine.circuit.terminal.TerminalImpl
import java.util.UUID

/**
 * A network of the logic circuit.
 */
public interface Network : CircuitElement {

    /**
     * Returns all nodes that are part of the network.
     */
    public fun nodes(): Collection<NetworkNode>

    /**
     * Returns the node with the given [uniqueId].
     */
    public fun node(uniqueId: UUID): NetworkNode?

    /**
     * Returns if the network contains the given [node].
     */
    public operator fun contains(node: NetworkNode): Boolean

    /**
     * Returns all connections that are part of the network.
     */
    public fun connections(): Collection<NetworkConnection>

    /**
     * Returns the connection between the nodes [nodeA] and [nodeB].
     */
    public fun connection(nodeA: NetworkNode, nodeB: NetworkNode): NetworkConnection?

    /**
     * Returns the connections between the nodes with UUIDs [nodeAUniqueId] and [nodeBUniqueId].
     */
    public fun connection(nodeAUniqueId: UUID, nodeBUniqueId: UUID): NetworkConnection?

    /**
     * Returns if the nodes [nodeA] and [nodeB] are connected.
     */
    public fun areConnected(nodeA: NetworkNode, nodeB: NetworkNode): Boolean

    /**
     * Returns all component ports that are part of the network.
     */
    public fun componentPorts(): Collection<ComponentPort>

    /**
     * Returns all terminals that are part of the network.
     */
    public fun terminals(): Collection<Terminal>
}

internal class NetworkImpl(
    override val circuit: CircuitImpl,
    override val uniqueId: UUID,
) : CircuitElementImpl(), Network {

    private val nodes: MutableSet<NetworkNode> = mutableSetOf()

    override fun nodes(): Collection<NetworkNodeImpl> {
        return circuit.networkNodes().filter { it.network == this }
    }

    override fun node(uniqueId: UUID): NetworkNode? {
        val node = circuit.networkNode(uniqueId) ?: return null
        if (node.network != this) {
            return null
        }
        return node
    }

    fun registerNode(node: NetworkNodeImpl) {
        node.network = this
        nodes += node
    }

    fun unregisterNode(node: NetworkNodeImpl) {
        nodes -= node
    }

    override fun contains(node: NetworkNode): Boolean {
        return node in nodes
    }

    override fun connections(): Collection<NetworkConnectionImpl> {
        return circuit.networkConnections().filter { it.network == this }
    }

    override fun connection(nodeA: NetworkNode, nodeB: NetworkNode): NetworkConnectionImpl? {
        if (nodeA.network != this || nodeB.network != this) {
            return null
        }
        return circuit.networkConnection(nodeA, nodeB)
    }

    override fun connection(nodeAUniqueId: UUID, nodeBUniqueId: UUID): NetworkConnectionImpl? {
        val connection = circuit.networkConnection(nodeAUniqueId, nodeBUniqueId) ?: return null
        if (connection.node1.network != this || connection.node2.network != this) {
            return null
        }
        return connection
    }

    override fun areConnected(nodeA: NetworkNode, nodeB: NetworkNode): Boolean {
        return connection(nodeA, nodeB) != null
    }

    override fun componentPorts(): Collection<ComponentPortImpl> {
        return nodes().mapNotNull(NetworkNodeImpl::holder).filterIsInstance<ComponentPortImpl>()
    }

    override fun terminals(): Collection<Terminal> {
        return nodes().mapNotNull(NetworkNodeImpl::holder).filterIsInstance<TerminalImpl>()
    }

    override fun remove() {
        circuit.removeNetwork(this)
    }

    fun destroy() {
        nodes.clear()
    }
}
