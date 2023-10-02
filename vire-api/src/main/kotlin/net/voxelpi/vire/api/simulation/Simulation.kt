package net.voxelpi.vire.api.simulation

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.event.SimulationEventService
import net.voxelpi.vire.api.simulation.network.Network
import net.voxelpi.vire.api.simulation.network.NetworkNode
import net.voxelpi.vire.api.simulation.network.NetworkState
import java.util.UUID

/**
 * The simulation that manages the state of all components and networks.
 */
interface Simulation {

    /**
     * The event service of the simulation.
     */
    val eventService: SimulationEventService

    /**
     * Returns a collection of all registered state machines.
     */
    fun stateMachines(): Collection<StateMachine>

    /**
     * Returns the state machine with the specified [identifier].
     * If no state machine with such key exists, `null` is returned.
     */
    fun stateMachine(identifier: Identifier): StateMachine?

    /**
     * Returns a collection of all registered components.
     */
    fun components(): Collection<Component>

    /**
     * Returns the component with the given [uniqueId].
     * If no component with such uuid exists, `null` is returned.
     */
    fun component(uniqueId: UUID): Component?

    /**
     * Creates a new component with the given [stateMachine].
     */
    fun createComponent(stateMachine: StateMachine): Component

    /**
     * Returns a collection of all registered networks.
     */
    fun networks(): Collection<Network>

    /**
     * Returns the network with the given [uniqueId].
     */
    fun network(uniqueId: UUID): Network?

    /**
     * Creates a new network with the given [uniqueId] and [state].
     */
    fun createNetwork(uniqueId: UUID = UUID.randomUUID(), state: NetworkState = NetworkState.None): Network

    /**
     * Returns the network node with the given [uniqueId].
     */
    fun networkNode(uniqueId: UUID): NetworkNode?

    /**
     * Creates a new network node with the given [uniqueId] in the given [network].
     */
    fun createNetworkNode(network: Network = createNetwork(), uniqueId: UUID = UUID.randomUUID()): NetworkNode

    /**
     * Creates a new network node with the given [uniqueId] that is connected to all the nodes in [connectedTo].
     * If the nodes in connected to are in different networks, these networks will be merged.
     */
    fun createNetworkNode(connectedTo: Collection<NetworkNode>, uniqueId: UUID = UUID.randomUUID()): NetworkNode

    /**
     * Simulates [numberOfSteps] steps.
     */
    fun simulateSteps(numberOfSteps: Int)
}
