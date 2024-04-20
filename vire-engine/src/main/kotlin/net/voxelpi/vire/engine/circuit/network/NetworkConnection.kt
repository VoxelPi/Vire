package net.voxelpi.vire.engine.circuit.network

import java.util.Objects

/**
 * A connection between two nodes in a network.
 */
public interface NetworkConnection {

    public val node1: NetworkNode

    public val node2: NetworkNode
}

// TODO: Maybe only store uniqueId of both nodes?
internal class NetworkConnectionImpl(
    nodeA: NetworkNode,
    nodeB: NetworkNode,
) : NetworkConnection {

    override val node1: NetworkNode

    override val node2: NetworkNode

    init {
        require(nodeA != nodeB) { "Reflective connections are not allowed." }

        if (nodeA.hashCode() >= nodeB.hashCode()) {
            node1 = nodeA
            node2 = nodeB
        } else {
            node1 = nodeB
            node2 = nodeA
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NetworkConnectionImpl

        return (node1.uniqueId == other.node1.uniqueId) && (node2.uniqueId == other.node2.uniqueId)
    }

    override fun hashCode(): Int {
        return Objects.hash(node1, node2)
    }
}
