package net.voxelpi.vire.engine.simulation

import net.voxelpi.event.EventScope
import net.voxelpi.event.eventScope
import net.voxelpi.event.post
import net.voxelpi.vire.api.simulation.Circuit
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.event.component.ComponentCreateEvent
import net.voxelpi.vire.api.simulation.event.component.ComponentDestroyEvent
import net.voxelpi.vire.api.simulation.event.network.NetworkCreateEvent
import net.voxelpi.vire.api.simulation.event.network.NetworkDestroyEvent
import net.voxelpi.vire.api.simulation.event.network.NetworkMergeEvent
import net.voxelpi.vire.api.simulation.event.network.NetworkSplitEvent
import net.voxelpi.vire.api.simulation.event.network.node.NetworkNodeCreateEvent
import net.voxelpi.vire.api.simulation.event.network.node.NetworkNodeDestroyEvent
import net.voxelpi.vire.api.simulation.network.Network
import net.voxelpi.vire.api.simulation.network.NetworkNode
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.engine.simulation.component.VireComponent
import net.voxelpi.vire.engine.simulation.network.VireNetwork
import net.voxelpi.vire.engine.simulation.network.VireNetworkNode
import net.voxelpi.vire.engine.simulation.statemachine.VireStateMachine
import org.slf4j.LoggerFactory
import java.util.UUID

class VireCircuit(
    override val simulation: VireSimulation,
) : Circuit {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override val eventScope: EventScope = simulation.eventScope.createSubScope()

    private val components: MutableMap<UUID, VireComponent> = mutableMapOf()
    private val networks: MutableMap<UUID, VireNetwork> = mutableMapOf()
    private val networkNodes: MutableMap<UUID, VireNetworkNode> = mutableMapOf()

    override fun components(): List<VireComponent> {
        return components.values.toList()
    }

    override fun component(uniqueId: UUID): VireComponent? {
        return components[uniqueId]
    }

    override fun createComponent(stateMachine: StateMachine): VireComponent {
        require(stateMachine is VireStateMachine)

        // Create the component.
        val component = VireComponent(this, stateMachine)
        components[component.uniqueId] = component

        // Fire the event.
        eventScope.post(ComponentCreateEvent(component))

        // Return the created component.
        return component
    }

    override fun removeComponent(component: Component) {
        require(component is VireComponent)

        // Fire the event.
        eventScope.post(ComponentDestroyEvent(component))

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

    override fun createNetwork(uniqueId: UUID, state: LogicState): VireNetwork {
        // Create the network.
        val network = VireNetwork(this, uniqueId, state)
        registerNetwork(network)

        // Publish event.
        eventScope.post(NetworkCreateEvent(network))

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
        eventScope.post(NetworkDestroyEvent(network))

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
        eventScope.post(NetworkNodeCreateEvent(node))

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
        eventScope.post(NetworkNodeDestroyEvent(node))

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

        eventScope.post(NetworkSplitEvent(node.network, networks))
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
            eventScope.post(NetworkMergeEvent(nodeA.network, listOf(networkA, networkB)))
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
        eventScope.post(NetworkSplitEvent(oldNetwork, listOf(networkA, networkB)))
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
        require(networks.isNotEmpty()) { "networks may not be empty" }
        var network = networks.first()

        // If more than one network is present merge them with the first network.
        if (networks.size > 1) {
            for (other in networks.drop(1)) {
                network = mergeNetworks(network, other)
            }

            // Publish event.
            eventScope.post(NetworkMergeEvent(network, networks))
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
        val network = createNetwork(state = LogicState.merge(network1.state, network2.state))

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

    fun simulateStep() {
        // Pull inputs from their assigned networks.
        components().forEach(VireComponent::pullInputs)

        // Run state machines.
        components().forEach(VireComponent::tick)

        // Reset network states.
        for (network in networks()) {
            network.state = LogicState.EMPTY
        }

        // Push outputs to their assigned networks.
        components().forEach(VireComponent::pushOutputs)
    }

    override fun clear() {
        components.clear()
        networks.clear()
    }
}
