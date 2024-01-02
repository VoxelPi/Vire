package net.voxelpi.vire.api.simulation.network

import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.SimulationObject
import net.voxelpi.vire.api.simulation.component.ComponentPort
import java.util.UUID

/**
 * A network of the logic simulation.
 */
interface Network : SimulationObject {

    /**
     * The state of the network.
     */
    val state: LogicState

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
     * Remove the given [node] from the network.
     */
    fun removeNode(node: NetworkNode)

    /**
     * Returns a collection of all component ports that are part of the network.
     */
    fun ports(): Collection<ComponentPort>

    /**
     * Pushes the state of all connected output variables to the network.
     * @return The resulting [LogicState].
     */
    fun pushPortOutputs(): LogicState
}
