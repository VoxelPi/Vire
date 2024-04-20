package net.voxelpi.vire.engine.circuit.network

import net.voxelpi.vire.engine.circuit.CircuitElement
import net.voxelpi.vire.engine.circuit.CircuitElementImpl
import net.voxelpi.vire.engine.circuit.CircuitImpl
import java.util.UUID

/**
 * A node in a network.
 */
public interface NetworkNode : CircuitElement {

    /**
     * The network the node belongs to.
     */
    public val network: Network

    /**
     * Returns all nodes that are directly connected to this node.
     */
    public fun connectedNodes(): Collection<NetworkNode>

    /**
     * Checks if this node is directly connected to the given [node]
     */
    public fun isConnectedTo(node: NetworkNode): Boolean
}

internal class NetworkNodeImpl(
    override val circuit: CircuitImpl,
    override var network: NetworkImpl = circuit.createNetwork(),
    override val uniqueId: UUID = UUID.randomUUID(),
    var holder: NetworkNodeHolder? = null,
) : CircuitElementImpl(), NetworkNode {

    private val connectedNodes: MutableSet<UUID> = mutableSetOf()

    fun registerConnection(node: NetworkNode) {
        connectedNodes.add(node.uniqueId)
    }

    fun unregisterConnection(node: NetworkNode) {
        connectedNodes.remove(node.uniqueId)
    }

    override fun connectedNodes(): Collection<NetworkNode> {
        return connectedNodes.mapNotNull(circuit::networkNode)
    }

    override fun isConnectedTo(node: NetworkNode): Boolean {
        return this.network == node.network && this.connectedNodes.contains(node.uniqueId)
    }

    override fun remove() {
        TODO("Not yet implemented")
    }
}
