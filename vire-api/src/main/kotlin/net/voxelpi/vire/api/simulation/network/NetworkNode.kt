package net.voxelpi.vire.api.simulation.network

import net.voxelpi.vire.api.simulation.CircuitElement

/**
 * A node in a network.
 */
interface NetworkNode : CircuitElement {

    /**
     * The network the node belongs to.
     */
    val network: Network

    /**
     * Returns all nodes that are currently connected to this node.
     */
    fun connectedNodes(): Collection<NetworkNode>

    /**
     * Checks if the node is directly connected to the given [node].
     */
    fun isDirectlyConnectedTo(node: NetworkNode): Boolean

    /**
     * Checks if the node is connected to the given [node] via any path.
     */
    fun isConnectedTo(node: NetworkNode): Boolean
}
