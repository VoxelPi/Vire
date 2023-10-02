package net.voxelpi.vire.api.simulation.network

import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.api.simulation.component.ComponentPort
import java.util.UUID

/**
 * A network of the logic simulation.
 */
interface Network {

    /**
     * The simulation the network belongs to.
     */
    val simulation: Simulation

    /**
     * The unique id of the network.
     */
    val uniqueId: UUID

    /**
     * The state of the network.
     */
    val state: NetworkState

    /**
     * Returns all nodes that are part of the network.
     */
    fun nodes(): Collection<NetworkNode>

    /**
     * Returns if the network contains the given [node].
     */
    operator fun contains(node: NetworkNode): Boolean

    /**
     * Creates a new network node.
     */
    fun createNode(connectedTo: Collection<NetworkNode>, uniqueId: UUID = UUID.randomUUID()): NetworkNode

    /**
     * Returns a collection of all component ports that are part of the network.
     */
    fun ports(): Collection<ComponentPort>

    /**
     * Pushes the state of all connected output variables to the network.
     */
    fun pushPortOutputs(): NetworkState
}
