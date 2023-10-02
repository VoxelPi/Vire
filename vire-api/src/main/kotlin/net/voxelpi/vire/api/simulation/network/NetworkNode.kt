package net.voxelpi.vire.api.simulation.network

import net.voxelpi.vire.api.simulation.Simulation
import java.util.UUID

/**
 * A node in a network.
 */
interface NetworkNode {

    /**
     * The simulation the network node belongs to.
     */
    val simulation: Simulation

    /**
     * The unique id of the node.
     */
    val uniqueId: UUID

    /**
     * The network the node belongs to.
     */
    val network: Network

    /**
     * Returns all nodes that are currently connected to this node.
     */
    fun connectedNodes(): Collection<NetworkNode>
}
