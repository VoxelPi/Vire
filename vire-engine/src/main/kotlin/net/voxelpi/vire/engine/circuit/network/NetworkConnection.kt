package net.voxelpi.vire.engine.circuit.network

import java.util.Objects
import java.util.UUID

/**
 * A connection between two nodes in a network.
 */
public interface NetworkConnection {

    public val node1: NetworkNode

    public val node2: NetworkNode

    public val network: Network
}

internal class NetworkConnectionImpl(
    nodeA: NetworkNodeImpl,
    nodeB: NetworkNodeImpl,
) : NetworkConnection {

    override val node1: NetworkNodeImpl

    override val node2: NetworkNodeImpl

    init {
        require(nodeA != nodeB) { "Reflective connections are not allowed" }
        require(nodeA.network == nodeB.network) { "Only nodes in the same network can be connected" }

        if (nodeA.hashCode() >= nodeB.hashCode()) {
            node1 = nodeA
            node2 = nodeB
        } else {
            node1 = nodeB
            node2 = nodeA
        }
    }

    override val network: NetworkImpl
        get() = node1.network

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NetworkConnectionImpl

        return (node1.uniqueId == other.node1.uniqueId) && (node2.uniqueId == other.node2.uniqueId)
    }

    override fun hashCode(): Int {
        return Objects.hash(node1, node2)
    }

    fun index(): Pair<UUID, UUID> {
        return Pair(node1.uniqueId, node2.uniqueId)
    }
}
