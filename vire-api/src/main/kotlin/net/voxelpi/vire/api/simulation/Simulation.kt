package net.voxelpi.vire.api.simulation

import kotlinx.coroutines.CoroutineScope
import net.voxelpi.event.EventScope
import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.library.Library
import net.voxelpi.vire.api.simulation.network.Network
import net.voxelpi.vire.api.simulation.network.NetworkNode
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInstance
import net.voxelpi.vire.api.simulation.statemachine.StateMachineProvider
import java.util.UUID

/**
 * The simulation that manages the state of all components and networks.
 */
interface Simulation {

    /**
     * The event scope of the simulation.
     */
    val eventScope: EventScope

    /**
     * The [CoroutineScope] of the simulation
     */
    val coroutineScope: CoroutineScope

    /**
     * Returns all registered libraries.
     */
    fun libraries(): Collection<Library>

    /**
     * Returns the [Library] with the given [id].
     */
    fun library(id: String): Library?

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
     * Creates a new state machine instance for the given [stateMachine].
     * The parameters of the instance are configured using the specified [configuration].
     */
    fun createStateMachineInstance(
        stateMachine: StateMachine,
        configuration: StateMachineInstance.ConfigurationContext.() -> Unit = {},
    ): StateMachineInstance

    /**
     * Creates a new state machine instance for the state machine provided by the given [stateMachineProvider].
     * The parameters of the instance are configured using the specified [configuration].
     */
    fun createStateMachineInstance(
        stateMachineProvider: StateMachineProvider,
        configuration: StateMachineInstance.ConfigurationContext.() -> Unit = {},
    ): StateMachineInstance

    /**
     * Creates a new state machine instance for the given [stateMachine].
     * The parameters of the instance are configured using the specified [configuration].
     * Whilst Not all parameters must be specified, only existing parameters may be specified.
     */
    fun createStateMachineInstance(
        stateMachine: StateMachine,
        configuration: Map<String, Any?>,
    ): StateMachineInstance

    /**
     * Creates a new state machine instance for the state machine provided by the given [stateMachineProvider].
     * The parameters of the instance are configured using the specified [configuration].
     * Whilst Not all parameters must be specified, only existing parameters may be specified.
     */
    fun createStateMachineInstance(
        stateMachineProvider: StateMachineProvider,
        configuration: Map<String, Any?>,
    ): StateMachineInstance

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
     * Removes the given [component] from the simulation.
     */
    fun removeComponent(component: Component)

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
    fun createNetwork(uniqueId: UUID = UUID.randomUUID(), state: LogicState = LogicState.EMPTY): Network

    /**
     * Removes the given [network] and all its nodes from the simulation.
     */
    fun removeNetwork(network: Network) {}

    /**
     * Returns a collection of all registered network nodes.
     */
    fun networkNodes(): Collection<NetworkNode>

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
     * Removes the given [node] from the simulation.
     */
    fun removeNetworkNode(node: NetworkNode)

    /**
     * Checks if the two given nodes [nodeA] and [nodeB] are connected directly with each other.
     */
    fun areNodesConnectedDirectly(nodeA: NetworkNode, nodeB: NetworkNode): Boolean

    /**
     * Checks if the two given nodes [nodeA] and [nodeB] are connected via any path.
     */
    fun areNodesConnected(nodeA: NetworkNode, nodeB: NetworkNode): Boolean

    /**
     * Connects the given nodes [nodeA] and [nodeB] with each other.
     * If the nodes are in different networks, the networks get merged.
     */
    fun createNetworkNodeConnection(nodeA: NetworkNode, nodeB: NetworkNode)

    /**
     * Removes the connection between the given nodes [nodeA] and [nodeB].
     * If this is the only connection between the remaining nodes, the network gets split.
     */
    fun removeNetworkNodeConnection(nodeA: NetworkNode, nodeB: NetworkNode)

    /**
     * Simulates [numberOfSteps] steps.
     */
    fun simulateSteps(numberOfSteps: Int)

    /**
     * Removes all registered components and networks.
     */
    fun clear()
}
