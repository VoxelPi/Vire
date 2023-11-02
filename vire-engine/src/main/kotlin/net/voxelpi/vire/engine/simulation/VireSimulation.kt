package net.voxelpi.vire.engine.simulation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.event.SimulationEvent
import net.voxelpi.vire.api.simulation.event.simulation.component.ComponentCreateEvent
import net.voxelpi.vire.api.simulation.event.simulation.component.ComponentDestroyEvent
import net.voxelpi.vire.api.simulation.event.simulation.network.NetworkCreateEvent
import net.voxelpi.vire.api.simulation.event.simulation.network.NetworkDestroyEvent
import net.voxelpi.vire.api.simulation.event.simulation.network.NetworkMergeEvent
import net.voxelpi.vire.api.simulation.event.simulation.network.NetworkSplitEvent
import net.voxelpi.vire.api.simulation.event.simulation.network.node.NetworkNodeCreateEvent
import net.voxelpi.vire.api.simulation.event.simulation.network.node.NetworkNodeDestroyEvent
import net.voxelpi.vire.api.simulation.library.Library
import net.voxelpi.vire.api.simulation.network.Network
import net.voxelpi.vire.api.simulation.network.NetworkNode
import net.voxelpi.vire.api.simulation.network.NetworkState
import net.voxelpi.vire.engine.simulation.component.VireComponent
import net.voxelpi.vire.engine.simulation.network.VireNetwork
import net.voxelpi.vire.engine.simulation.network.VireNetworkNode
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.reflect.KClass

class VireSimulation(
    libraries: List<Library>,
) : Simulation {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val libraries: Map<String, Library>
    private val stateMachines: Map<Identifier, StateMachine>

    private val components: MutableMap<UUID, VireComponent> = mutableMapOf()
    private val networks: MutableMap<UUID, VireNetwork> = mutableMapOf()
    private val networkNodes: MutableMap<UUID, VireNetworkNode> = mutableMapOf()

    private val eventsFlow: MutableSharedFlow<SimulationEvent> = MutableSharedFlow()

    override val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val events: SharedFlow<SimulationEvent>
        get() = eventsFlow.asSharedFlow()

    init {
        // Register libraries
        this.libraries = libraries.associateBy { it.id }
        logger.info("Loaded ${libraries.size} libraries: ${libraries.joinToString(", ", "[", "]") { it.name }}")

        // Register state machines
        val stateMachines = mutableMapOf<Identifier, StateMachine>()
        this.libraries.values.forEach { stateMachines.putAll(it.stateMachines().associateBy(StateMachine::identifier)) }
        this.stateMachines = stateMachines
        logger.info("Registered ${stateMachines.size} state machines")
    }

    override fun libraries(): List<Library> {
        return libraries.values.toList()
    }

    override fun library(id: String): Library? {
        return libraries[id]
    }

    override fun stateMachine(identifier: Identifier): StateMachine? {
        return stateMachines[identifier]
    }

    override fun stateMachines(): List<StateMachine> {
        return stateMachines.values.toList()
    }

    override fun components(): List<VireComponent> {
        return components.values.toList()
    }

    override fun component(uniqueId: UUID): VireComponent? {
        return components[uniqueId]
    }

    override fun createComponent(stateMachine: StateMachine): VireComponent {
        // Create the component.
        val component = VireComponent(this, stateMachine)
        components[component.uniqueId] = component

        // Fire the event.
        publish(ComponentCreateEvent(component))

        // Return the created component.
        return component
    }

    override fun removeComponent(component: Component) {
        require(component is VireComponent)

        // Fire the event.
        publish(ComponentDestroyEvent(component))

        // Destroy the port.
        components.remove(component.uniqueId)
        component.destroy()
    }

    override fun networks(): List<VireNetwork> {
        return networks.values.toList()
    }

    override fun network(uniqueId: UUID): VireNetwork? {
        return networks[uniqueId]
    }

    override fun createNetwork(uniqueId: UUID, state: NetworkState): VireNetwork {
        // Create the network.
        val network = VireNetwork(this, uniqueId, state)
        registerNetwork(network)

        // Publish event.
        publish(NetworkCreateEvent(network))

        // Return the created network.
        return network
    }

    override fun removeNetwork(network: Network) {
        require(network is VireNetwork)

        // Network has already been removed.
        if (network.uniqueId !in networks) {
            return
        }

        // Publish event.
        publish(NetworkDestroyEvent(network))

        // Remove all nodes.
        val nodes = network.nodes()
        for (node in nodes) {
            if (node.holder != null) {
                node.network = createNetwork()
                node.network.pushPortOutputs()
            } else {
                unregisterNetworkNode(node)
            }
        }

        // Remove the network.
        network.destroy()
        unregisterNetwork(network)
    }

    private fun registerNetwork(network: VireNetwork) {
        networks[network.uniqueId] = network
    }

    private fun unregisterNetwork(network: VireNetwork) {
        networks.remove(network.uniqueId)
    }

    override fun networkNodes(): List<NetworkNode> {
        return networkNodes.values.toList()
    }

    override fun networkNode(uniqueId: UUID): VireNetworkNode? {
        return networkNodes[uniqueId]
    }

    private fun registerNetworkNode(node: VireNetworkNode) {
        networkNodes[node.uniqueId] = node
    }

    private fun unregisterNetworkNode(node: VireNetworkNode) {
        networkNodes.remove(node.uniqueId)
    }

    override fun createNetworkNode(network: Network, uniqueId: UUID): VireNetworkNode {
        // Create the network node.
        val node = VireNetworkNode(this, network as VireNetwork, uniqueId)
        registerNetworkNode(node)

        // Publish event.
        publish(NetworkNodeCreateEvent(node))

        // Return the created network node.
        return node
    }

    override fun createNetworkNode(connectedTo: Collection<NetworkNode>, uniqueId: UUID): NetworkNode {
        // Check if the created node should connect to any node. If not, create the node in a new network instead.
        if (networks.isEmpty()) {
            return createNetworkNode(createNetwork(), uniqueId)
        }

        // Get all networks that this node should connect to and merge them together.
        val networks = connectedTo.distinctBy { it.network.uniqueId }.map { it.network }.filterIsInstance<VireNetwork>()
        val network = mergeNetworks(networks)

        // Add the node to the merged network.
        return network.createNode(connectedTo, uniqueId)
    }

    override fun removeNetworkNode(node: NetworkNode) {
        require(node is VireNetworkNode)

        // Skip if the node already has been removed.
        if (node !in node.network) {
            return
        }

        // Publish event.
        publish(NetworkNodeDestroyEvent(node))

        // Remove node from its network.
        node.network.unregisterNode(node)
        unregisterNetworkNode(node)

        // If the block was the last node of the network, remove the network.
        if (node.network.nodes().isEmpty()) {
            unregisterNetwork(node.network)
            return
        }

        // Query all connected network nodes.
        val connectedNodes = node.connectedNodes()

        // Remove all connections to the node.
        for (connectedNode in node.connectedNodes()) {
            connectedNode.unregisterConnection(node)
            node.unregisterConnection(connectedNode)
        }

        // Generate new node groups.
        val groups = mutableListOf<MutableSet<UUID>>()
        for (connectedNode in connectedNodes) {
            // Skip if the node is already counted.
            if (groups.any { it.contains(connectedNode.uniqueId) }) {
                continue
            }

            // Create a new group.
            val collected = mutableSetOf(connectedNode.uniqueId)
            collectConnectedNodes(connectedNode, collected)
            groups.add(collected)
        }

        // Return if all connected nodes are still connected via another path.
        if (groups.size == 1) {
            return
        }

        // Reassign networks.
        val networks = groups.map { createNetwork(state = node.network.state) }
        for (networkNode in node.network.nodes().toList()) {
            networkNode.network = networks[groups.indexOfFirst { it.contains(networkNode.uniqueId) }]
        }
        unregisterNetwork(node.network)

        // Update network states
        for (network in networks) {
            network.pushPortOutputs()
        }

        publish(NetworkSplitEvent(node.network, networks))
    }

    override fun areNodesConnectedDirectly(nodeA: NetworkNode, nodeB: NetworkNode): Boolean {
        return nodeA.isDirectlyConnectedTo(nodeB)
    }

    override fun areNodesConnected(nodeA: NetworkNode, nodeB: NetworkNode): Boolean {
        require(nodeA is VireNetworkNode)
        require(nodeB is VireNetworkNode)

        // Check if the nodes are connected directly.
        if (areNodesConnectedDirectly(nodeA, nodeB)) {
            return true
        }

        val checkedNodes = mutableSetOf<UUID>() // Nodes that already have been checked.
        val nodesToCheck = nodeA.connectedNodes().toMutableList() // Nodes that should be checked in the next iteration.
        val nodesToCheckNext = mutableListOf<VireNetworkNode>()
        while (nodesToCheck.isNotEmpty()) {
            for (node in nodesToCheck) {
                // Loop over all connected nodes that have not yet been checked.
                for (connectedNode in node.connectedNodes().filter { it.uniqueId !in checkedNodes }) {
                    // Check if the node is the searched node.
                    if (connectedNode == nodeB) {
                        return true
                    }

                    // Check the nodes connections in the next iteration.
                    nodesToCheckNext.add(connectedNode)
                }
            }

            // Move checked nodes to checkedNodes set and setup next iteration.
            checkedNodes.addAll(nodesToCheck.map { it.uniqueId })
            nodesToCheck.clear()
            nodesToCheck.addAll(nodesToCheckNext)
        }

        // The node could not be reached, therefore return false.
        return false
    }

    override fun createNetworkNodeConnection(nodeA: NetworkNode, nodeB: NetworkNode) {
        // Check if the two nodes are already connected.
        if (nodeA.isDirectlyConnectedTo(nodeB)) {
            return
        }

        // Check if the two nodes are in the same network. If not, merge the two networks.
        require(nodeA is VireNetworkNode)
        require(nodeB is VireNetworkNode)
        val networkA = nodeA.network
        val networkB = nodeB.network
        if (networkA != networkB) {
            mergeNetworks(nodeA.network, nodeB.network)
        }

        // Connect the nodes with each other.
        nodeA.registerConnection(nodeB)
        nodeB.registerConnection(nodeA)

        // Publish event.
        if (networkA != networkB) {
            publish(NetworkMergeEvent(nodeA.network, listOf(networkA, networkB)))
        }
    }

    override fun removeNetworkNodeConnection(nodeA: NetworkNode, nodeB: NetworkNode) {
        // Check if the two nodes are not connected.
        if (!nodeA.isDirectlyConnectedTo(nodeB)) {
            return
        }

        // Remove connection
        require(nodeA is VireNetworkNode)
        require(nodeB is VireNetworkNode)
        nodeA.unregisterConnection(nodeB)
        nodeB.unregisterConnection(nodeA)

        // Return if the nodes are still connected, otherwise the network has to be split.
        if (areNodesConnected(nodeA, nodeB)) {
            return
        }

        val oldNetwork = nodeA.network

        // Create a new group.
        val networkANodes = mutableSetOf(nodeA.uniqueId)
        val networkBNodes = mutableSetOf(nodeB.uniqueId)
        collectConnectedNodes(nodeA, networkANodes)
        collectConnectedNodes(nodeB, networkBNodes)

        // Reassign networks.
        val networkA = createNetwork(state = oldNetwork.state)
        val networkB = createNetwork(state = oldNetwork.state)
        for (networkNode in oldNetwork.nodes().toList()) {
            networkNode.network = when (networkNode.uniqueId) {
                in networkANodes -> networkA
                in networkBNodes -> networkB
                else -> throw IllegalStateException("Connected node is in no node pool")
            }
        }
        unregisterNetwork(oldNetwork)

        // Update network states
        networkA.pushPortOutputs()
        networkB.pushPortOutputs()

        // Publish event.
        publish(NetworkSplitEvent(oldNetwork, listOf(networkA, networkB)))
    }

    private fun collectConnectedNodes(networkNode: VireNetworkNode, collected: MutableSet<UUID>) {
        for (connectedNode in networkNode.connectedNodes()) {
            // Skip, if the node has already been counted.
            if (connectedNode.uniqueId in collected) {
                continue
            }

            // Add node and its connections to the set.
            collected.add(connectedNode.uniqueId)
            collectConnectedNodes(connectedNode, collected)
        }
    }

    private fun mergeNetworks(networks: List<VireNetwork>): VireNetwork {
        // Select the first network.
        require(networks.isNotEmpty())
        var network = networks.first()

        // If more than one network is present merge them with the first network.
        if (networks.size > 1) {
            for (other in networks.drop(1)) {
                network = mergeNetworks(network, other)
            }

            // Publish event.
            publish(NetworkMergeEvent(network, networks))
        }

        // Return the merged network.
        return network
    }

    private fun mergeNetworks(network1: VireNetwork, network2: VireNetwork): VireNetwork {
        // Check if the two networks are the same.
        if (network1 == network2) {
            return network1
        }

        // Create a new network.
        val network = createNetwork(state = NetworkState.merge(network1.state, network2.state))

        // Add all nodes of the previous two networks to the new network.
        for (node in network1.nodes()) {
            node.network = network
        }
        for (node in network2.nodes()) {
            node.network = network
        }

        // Unregister old networks.
        unregisterNetwork(network1)
        unregisterNetwork(network2)

        // Update network states
        network.pushPortOutputs()

        return network
    }

    private fun simulateStep() {
        // Pull inputs from their assigned networks.
        components().forEach(VireComponent::pullInputs)

        // Run state machines.
        components().forEach(VireComponent::tick)

        // Reset network states.
        for (network in networks()) {
            network.state = NetworkState.None
        }

        // Push outputs to their assigned networks.
        components().forEach(VireComponent::pushOutputs)
    }

    override fun simulateSteps(numberOfSteps: Int) {
        for (i in 1..numberOfSteps) {
            simulateStep()
        }
    }

    override fun clear() {
        components.clear()
        networks.clear()
    }

    override fun <T : SimulationEvent> subscribe(type: KClass<T>, scope: CoroutineScope, consumer: suspend T.() -> Unit): Job {
        return events
            .filterIsInstance(type)
            .onEach { event ->
                scope.launch { runCatching { consumer(event) }.onFailure { logger.error("Unable to process event", it) } }
            }
            .launchIn(scope)
    }

    fun publish(event: SimulationEvent) {
        runBlocking {
            eventsFlow.emit(event)
        }
    }

    fun flushEvents() {
        runBlocking {
            eventsFlow.emit(object : SimulationEvent {
                override val simulation: Simulation
                    get() = this@VireSimulation
            })
        }
    }

    fun shutdown() {
        flushEvents()
        coroutineScope.cancel()
    }
}
