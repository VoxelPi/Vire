package net.voxelpi.vire.simulation

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.library.Library
import net.voxelpi.vire.api.simulation.network.Network
import net.voxelpi.vire.api.simulation.network.NetworkNode
import net.voxelpi.vire.api.simulation.network.NetworkState
import net.voxelpi.vire.simulation.component.VireComponent
import net.voxelpi.vire.simulation.event.VireSimulationEventService
import net.voxelpi.vire.simulation.network.VireNetwork
import net.voxelpi.vire.simulation.network.VireNetworkNode
import org.slf4j.LoggerFactory
import java.util.UUID

class VireSimulation(
    modules: List<Library>,
) : Simulation {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val modules: Map<String, Library>
    private val stateMachines: Map<Identifier, StateMachine>

    private val components: MutableMap<UUID, VireComponent> = mutableMapOf()
    private val networks: MutableMap<UUID, VireNetwork> = mutableMapOf()
    private val networkNodes: MutableMap<UUID, VireNetworkNode> = mutableMapOf()

    override val eventService: VireSimulationEventService = VireSimulationEventService()

    init {
        // Register modules
        this.modules = modules.associateBy { it.id }
        logger.info("Loaded ${modules.size} modules: ${modules.joinToString(", ", "[", "]") { it.name }}")

        // Register state machines
        val stateMachines = mutableMapOf<Identifier, StateMachine>()
        this.modules.values.forEach { stateMachines.putAll(it.stateMachines().associateBy(StateMachine::identifier)) }
        this.stateMachines = stateMachines
        logger.info("Registered ${stateMachines.size} state machines")
    }

    override fun stateMachine(identifier: Identifier): StateMachine? {
        return stateMachines[identifier]
    }

    override fun stateMachines(): Collection<StateMachine> {
        return stateMachines.values
    }

    override fun components(): Collection<VireComponent> {
        return components.values
    }

    override fun component(uniqueId: UUID): Component? {
        return components[uniqueId]
    }

    override fun createComponent(stateMachine: StateMachine): Component {
        val component = VireComponent(this, stateMachine)
        components[component.uniqueId] = component
        return component
    }

    override fun networks(): Collection<VireNetwork> {
        return networks.values
    }

    override fun network(uniqueId: UUID): VireNetwork? {
        return networks[uniqueId]
    }

    override fun createNetwork(uniqueId: UUID, state: NetworkState): VireNetwork {
        val network = VireNetwork(this, uniqueId, state)
        registerNetwork(network)
        return network
    }

    fun registerNetwork(network: VireNetwork) {
        networks[network.uniqueId] = network
    }

    fun unregisterNetwork(network: VireNetwork) {
        networks.remove(network.uniqueId)
    }

    override fun networkNode(uniqueId: UUID): VireNetworkNode? {
        return networkNodes[uniqueId]
    }

    fun registerNetworkNode(node: VireNetworkNode) {
        networkNodes[node.uniqueId] = node
    }

    fun unregisterNetworkNode(node: VireNetworkNode) {
        networkNodes.remove(node.uniqueId)
    }

    override fun createNetworkNode(network: Network, uniqueId: UUID): VireNetworkNode {
        val node = VireNetworkNode(this, network as VireNetwork, uniqueId)
        registerNetworkNode(node)
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

    fun removeNetworkNode(networkNode: VireNetworkNode) {
        // Remove node from its network.
        networkNode.network.unregisterNode(networkNode)

        // If the block was the last node of the network, remove the network.
        if (networkNode.network.nodes().isEmpty()) {
            unregisterNetwork(networkNode.network)
            return
        }

        // Query all connected network nodes.
        val connectedNodes = networkNode.connectedNodes()

        // Remove all connections to the node.
        for (connectedNode in networkNode.connectedNodes()) {
            connectedNode.unregisterConnection(networkNode)
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
        val networks = groups.map { createNetwork(state = networkNode.network.state) }
        for (node in networkNode.network.nodes().toList()) {
            node.network = networks[groups.indexOfFirst { it.contains(node.uniqueId) }]
        }
        unregisterNetwork(networkNode.network)

        // Update network states
        for (network in networks) {
            network.pushPortOutputs()
        }
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
        }

        // Return the merged network.
        return network
    }

    private fun mergeNetworks(network1: VireNetwork, network2: VireNetwork): VireNetwork {
        if (network1.uniqueId == network2.uniqueId) {
            return network1
        }

        val network = createNetwork(state = NetworkState.merge(network1.state, network2.state))
        val nodes = mutableListOf<VireNetworkNode>()
        nodes.addAll(network1.nodes())
        nodes.addAll(network2.nodes())
        for (node in nodes) {
            node.network = network
        }
        unregisterNetwork(network1)
        unregisterNetwork(network2)
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

    fun clear() {
        components.clear()
        networks.clear()
    }
}
