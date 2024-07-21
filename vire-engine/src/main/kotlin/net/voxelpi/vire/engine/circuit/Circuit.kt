package net.voxelpi.vire.engine.circuit

import net.voxelpi.event.EventScope
import net.voxelpi.event.post
import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.component.Component
import net.voxelpi.vire.engine.circuit.component.ComponentImpl
import net.voxelpi.vire.engine.circuit.event.component.ComponentCreateEvent
import net.voxelpi.vire.engine.circuit.event.component.ComponentDestroyEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkConnectionCreateEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkConnectionDestroyEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkCreateEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkDestroyEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkMergeEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkNodeCreateEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkNodeDestroyEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkSplitEvent
import net.voxelpi.vire.engine.circuit.event.terminal.TerminalCreateEvent
import net.voxelpi.vire.engine.circuit.event.terminal.TerminalDestroyEvent
import net.voxelpi.vire.engine.circuit.network.Network
import net.voxelpi.vire.engine.circuit.network.NetworkConnection
import net.voxelpi.vire.engine.circuit.network.NetworkConnectionImpl
import net.voxelpi.vire.engine.circuit.network.NetworkImpl
import net.voxelpi.vire.engine.circuit.network.NetworkNode
import net.voxelpi.vire.engine.circuit.network.NetworkNodeImpl
import net.voxelpi.vire.engine.circuit.terminal.Terminal
import net.voxelpi.vire.engine.circuit.terminal.TerminalImpl
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.environment.EnvironmentImpl
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.circuit.CircuitKernel
import net.voxelpi.vire.engine.kernel.variable.IOVariable
import net.voxelpi.vire.engine.kernel.variable.IOVectorVariable
import net.voxelpi.vire.engine.kernel.variable.InterfaceVariable
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VectorSizeInitializationContext
import net.voxelpi.vire.engine.kernel.variable.provider.MutableVectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableVectorSizeProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.mutableVectorSizeStorage
import net.voxelpi.vire.engine.util.GraphUtil
import java.util.UUID

/**
 * A logic circuit created by linking different components together.
 */
public interface Circuit : VariableProvider, MutableVectorSizeProvider {

    /**
     * The environment of the circuit.
     */
    public val environment: Environment

    /**
     * The event scope of the environment.
     */
    public val eventScope: EventScope

    /**
     * The tags of the circuit.
     */
    public val tags: MutableSet<Identifier>

    /**
     * The properties of the circuit.
     */
    public val properties: MutableMap<Identifier, String>

    /**
     * Add a new io variable on the circuit.
     */
    public fun <V : IOVariable> declareVariable(variable: V): V

    /**
     * Remove am io variable from the circuit.
     */
    public fun <V : IOVariable> removeVariable(variable: V): V?

    /**
     * Creates a circuit kernel variant from this circuit.
     */
    public fun createKernelVariant(): KernelVariant

    /**
     * Creates a new circuit instance.
     */
    public fun createCircuitInstance(settingStates: SettingStateProvider): Result<CircuitInstance>

    /**
     * Returns all registered components.
     */
    public fun components(): Collection<Component>

    /**
     * Returns the component with the given [uniqueId].
     * If no component with such uuid exists, `null` is returned.
     */
    public fun component(uniqueId: UUID): Component?

    /**
     * Creates a new component in the circuit.
     */
    public fun createComponent(kernelVariant: KernelVariant, uniqueId: UUID = UUID.randomUUID()): Component

    /**
     * Removes the given [component] from the circuit.
     */
    public fun removeComponent(component: Component)

    /**
     * Returns all registered terminals.
     */
    public fun terminals(): Collection<Terminal>

    /**
     * Returns the terminal with the given [uniqueId]
     */
    public fun terminal(uniqueId: UUID): Terminal?

    /**
     * Creates a new terminal with the given [uniqueId] in the circuit for the given [variable].
     */
    public fun createTerminal(variable: InterfaceVariable?, uniqueId: UUID = UUID.randomUUID()): Terminal

    /**
     * Removes the given [terminal] from the circuit.
     */
    public fun removeTerminal(terminal: Terminal)

    /**
     * Returns all registered networks.
     */
    public fun networks(): Collection<Network>

    /**
     * Returns the network with the given [uniqueId].
     */
    public fun network(uniqueId: UUID): Network?

    /**
     * Creates a new network.
     */
    public fun createNetwork(uniqueId: UUID = UUID.randomUUID()): Network

    /**
     * Creates a new network with a node for each entry in [nodes] and a connection for each entry in [connections].
     * If a unique id is already used by an existing node, that node is used instead of creating a new node.
     * Note that all nodes have to be connected with each other by some path.
     */
    public fun createNetwork(
        nodes: Collection<UUID>,
        connections: Collection<Pair<UUID, UUID>>,
        uniqueId: UUID = UUID.randomUUID(),
    ): Network

    /**
     * Removes the given [network] and all its nodes from the simulation.
     */
    public fun removeNetwork(network: Network)

    /**
     * Returns a collection of all registered network nodes.
     */
    public fun networkNodes(): Collection<NetworkNode>

    /**
     * Returns the network node with the given [uniqueId].
     */
    public fun networkNode(uniqueId: UUID): NetworkNode?

    /**
     * Creates a new network node with the given [uniqueId] in the given [network].
     */
    public fun createNetworkNode(network: Network = createNetwork(), uniqueId: UUID = UUID.randomUUID()): NetworkNode

    /**
     * Creates a new network node with the given [uniqueId] that is connected to all the nodes in [connectedTo].
     * If the nodes in connected to are in different networks, these networks will be merged.
     */
    public fun createNetworkNode(connectedTo: Collection<NetworkNode>, uniqueId: UUID = UUID.randomUUID()): NetworkNode

    /**
     * Creates a new network node with the given [uniqueId] that is connected to all the nodes in [connectedTo].
     * If the nodes in connected to are in different networks, these networks will be merged.
     */
    public fun createNetworkNode(vararg connectedTo: NetworkNode, uniqueId: UUID = UUID.randomUUID()): NetworkNode {
        return createNetworkNode(connectedTo.toList(), uniqueId)
    }

    /**
     * Removes the given [node] from the simulation.
     */
    public fun removeNetworkNode(node: NetworkNode)

    /**
     * Returns all network connections of the circuit.
     */
    public fun networkConnections(): Collection<NetworkConnection>

    /**
     * Returns the network connection between the nodes [nodeA] and [nodeB].
     */
    public fun networkConnection(nodeA: NetworkNode, nodeB: NetworkNode): NetworkConnection?

    /**
     * Returns the network connection between the nodes with UUIDs [nodeAUniqueId] and [nodeBUniqueId].
     */
    public fun networkConnection(nodeAUniqueId: UUID, nodeBUniqueId: UUID): NetworkConnection?

    /**
     * Creates a new connection between the given nodes [nodeA] and [nodeB].
     * If the two nodes are in different networks, the two networks get merged into a new one.
     */
    public fun createNetworkConnection(nodeA: NetworkNode, nodeB: NetworkNode): NetworkConnection

    /**
     * Removes the connection between the given nodes [nodeA] and [nodeB].
     * If the connection was a bridge, the network is split into the two remaining partitions.
     */
    public fun removeNetworkConnection(nodeA: NetworkNode, nodeB: NetworkNode)

    /**
     * Checks if there exists a connection between the two nodes [nodeA] and [nodeB].
     */
    public fun areNodesConnected(nodeA: NetworkNode, nodeB: NetworkNode): Boolean
}

internal class CircuitImpl(
    override val environment: EnvironmentImpl,
) : Circuit, MutableVectorSizeProviderWrapper {

    override val eventScope: EventScope = environment.eventScope.createSubScope()

    private val components: MutableMap<UUID, ComponentImpl> = mutableMapOf()
    private val terminals: MutableMap<UUID, TerminalImpl> = mutableMapOf()
    private val networks: MutableMap<UUID, NetworkImpl> = mutableMapOf()
    private val networkNodes: MutableMap<UUID, NetworkNodeImpl> = mutableMapOf()
    private val networkConnections: MutableMap<Pair<UUID, UUID>, NetworkConnectionImpl> = mutableMapOf()

    private val variables: MutableMap<String, Variable<*>> = mutableMapOf()
    private val parameterStateStorage = ParameterStateStorage(this, emptyMap())
    val vectorSizeStorage = mutableVectorSizeStorage(this, emptyMap())

    override val tags: MutableSet<Identifier> = mutableSetOf()
    override val properties: MutableMap<Identifier, String> = mutableMapOf()

    override fun variables(): Collection<Variable<*>> {
        return variables.values
    }

    override fun variable(name: String): Variable<*>? {
        return variables[name]
    }

    override fun <V : IOVariable> declareVariable(variable: V): V {
        require(variable.name !in variables) { "A variable with the name \"${variable.name}\" already exists for this circuit" }
        variables[variable.name] = variable
        if (variable is IOVectorVariable) {
            vectorSizeStorage.resize(variable, variable.size(VectorSizeInitializationContext(parameterStateStorage)))
        }
        return variable
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V : IOVariable> removeVariable(variable: V): V? {
        val existing = variables[variable.name] ?: return null
        require(existing == variable) { "Variable has different definition" }
        if (variable is IOVectorVariable) {
            vectorSizeStorage.unregister(variable)
        }
        return variables.remove(variable.name) as V?
    }

    override val vectorSizeProvider: MutableVectorSizeProvider
        get() = vectorSizeStorage

    override fun createKernelVariant(): KernelVariant {
        return CircuitKernel.createVariant(this)
    }

    override fun createCircuitInstance(settingStates: SettingStateProvider): Result<CircuitInstance> {
        return CircuitInstanceImpl.circuitInstance(this, settingStates)
    }

    override fun components(): Collection<ComponentImpl> {
        return components.values
    }

    override fun component(uniqueId: UUID): ComponentImpl? {
        return components[uniqueId]
    }

    override fun createComponent(kernelVariant: KernelVariant, uniqueId: UUID): ComponentImpl {
        require(kernelVariant is KernelVariantImpl)

        // Create the component.
        val component = ComponentImpl(this, kernelVariant, uniqueId)
        registerComponent(component)

        // Post event.
        eventScope.post(ComponentCreateEvent(component))

        // Return the created event.
        return component
    }

    override fun removeComponent(component: Component) {
        require(component is ComponentImpl)

        // Post event.
        eventScope.post(ComponentDestroyEvent(component))

        // Remove the component.
        component.destroy()
        unregisterComponent(component)
    }

    private fun registerComponent(component: ComponentImpl) {
        components[component.uniqueId] = component
    }

    private fun unregisterComponent(component: ComponentImpl) {
        components.remove(component.uniqueId)
    }

    override fun terminals(): Collection<Terminal> {
        return terminals.values
    }

    override fun terminal(uniqueId: UUID): Terminal? {
        return terminals[uniqueId]
    }

    override fun createTerminal(variable: InterfaceVariable?, uniqueId: UUID): Terminal {
        // Create the terminal.
        val terminal = TerminalImpl(this, variable, uniqueId)
        registerTerminal(terminal)

        // Post event.
        eventScope.post(TerminalCreateEvent(terminal))

        // Return the created terminal.
        return terminal
    }

    override fun removeTerminal(terminal: Terminal) {
        require(terminal is TerminalImpl)

        // Post event.
        eventScope.post(TerminalDestroyEvent(terminal))

        // Remove the terminal.
        terminal.destroy()
        unregisterTerminal(terminal)
    }

    private fun registerTerminal(terminal: TerminalImpl) {
        terminals[terminal.uniqueId] = terminal
    }

    private fun unregisterTerminal(terminal: TerminalImpl) {
        terminals.remove(terminal.uniqueId)
    }

    override fun networks(): Collection<NetworkImpl> {
        return networks.values
    }

    override fun network(uniqueId: UUID): NetworkImpl? {
        return networks[uniqueId]
    }

    override fun createNetwork(uniqueId: UUID): NetworkImpl {
        // Create the network
        val network = NetworkImpl(this, uniqueId)
        registerNetwork(network)

        // Publish event.
        eventScope.post(NetworkCreateEvent(network))

        // Return the created network.
        return network
    }

    override fun createNetwork(
        nodes: Collection<UUID>,
        connections: Collection<Pair<UUID, UUID>>,
        uniqueId: UUID,
    ): Network {
        val network = createNetwork()

        // Check that only specified nodes are used in connections.
        for ((node1UniqueId, node2UniqueId) in connections) {
            require(node1UniqueId in nodes) { "All nodes that are used in connections must be specified in the node set (node1)" }
            require(node2UniqueId in nodes) { "All nodes that are used in connections must be specified in the node set (node2)" }
        }

        // Check that the graph is connected
        require(GraphUtil.isConnectedGraph(nodes, connections)) { "All nodes must be connected to each other via some path" }

        // Find existing nodes and networks.
        val existingNodes = nodes.mapNotNull { networkNodes[it] }.toSet()
        val existingNetworks = existingNodes.map(NetworkNodeImpl::network).distinct()

        // Merge all existing networks.
        for (existingNetwork in existingNetworks) {
            mergeNetworksIntoFirst(network, existingNetwork)
        }

        // Create missing network nodes.
        for (nodeUniqueId in nodes) {
            if (networkNodes[nodeUniqueId] == null) {
                createNetworkNode(network, nodeUniqueId)
            }
        }

        // Create all connections
        for ((node1UniqueId, node2UniqueId) in connections) {
            // Get nodes (existence has already been checked)
            val node1 = networkNodes[node1UniqueId]!!
            val node2 = networkNodes[node2UniqueId]!!
            createNetworkConnection(node1, node2)
        }

        // Return the created network.
        return network
    }

    override fun removeNetwork(network: Network) {
        require(network is NetworkImpl)

        // Check if the network has already been removed.
        require(network.uniqueId in networks) { "The network is not part of this circuit." }

        // Publish event.
        eventScope.post(NetworkDestroyEvent(network))

        // Remove all nodes of the network or create a new network if the node has a holder.
        val nodes = network.nodes()
        for (node in nodes) {
            if (node.holder != null) {
                node.network = createNetwork()
            } else {
                unregisterNetworkNode(node)
                node.destroy()
            }
        }

        // Remove the network.
        network.destroy()
        unregisterNetwork(network)
    }

    private fun registerNetwork(network: NetworkImpl) {
        networks[network.uniqueId] = network
    }

    private fun unregisterNetwork(network: NetworkImpl) {
        networks.remove(network.uniqueId)
    }

    override fun networkNodes(): Collection<NetworkNodeImpl> {
        return networkNodes.values
    }

    override fun networkNode(uniqueId: UUID): NetworkNodeImpl? {
        return networkNodes[uniqueId]
    }

    override fun createNetworkNode(network: Network, uniqueId: UUID): NetworkNodeImpl {
        require(network is NetworkImpl)

        // Create the network node.
        val node = NetworkNodeImpl(this, network, uniqueId)
        registerNetworkNode(node)

        // Publish event
        eventScope.post(NetworkNodeCreateEvent(node))

        // Return the created node.
        return node
    }

    override fun createNetworkNode(connectedTo: Collection<NetworkNode>, uniqueId: UUID): NetworkNodeImpl {
        // Check if the created node should connect to any node. If not, create the node in a new network instead.
        if (connectedTo.isEmpty()) {
            return createNetworkNode(createNetwork(), uniqueId)
        }

        // Get all networks that this node should connect to and merge them together.
        val networks = connectedTo.distinctBy { it.network.uniqueId }.map { it.network }.filterIsInstance<NetworkImpl>()
        val network = mergeNetworks(networks)

        // Create the node.
        val node = createNetworkNode(network, uniqueId)

        // Create connections from the node to all connected nodes.
        for (connectedNode in connectedTo) {
            createNetworkConnection(node, connectedNode)
        }

        // Return the created network node.
        return node
    }

    override fun removeNetworkNode(node: NetworkNode) {
        require(node is NetworkNodeImpl)

        // Check that the node is part of the circuit
        require(node.uniqueId in networkNodes) { "The network node is not part of this circuit." }
        check(node in node.network) { "The network node is not part of its own network." }

        // Publish event.
        eventScope.post(NetworkNodeDestroyEvent(node))

        // Remove the node from its network.
        unregisterNetworkNode(node)

        // If the block was the last node of the network, remove the network.
        if (node.network.nodes().isEmpty()) {
            unregisterNetwork(node.network)
            return
        }

        // Query all connected network nodes.
        val connections = node.connections()
        val connectedNodes = node.connectedNodes()

        // Remove all connections to the node.
        for (connection in connections) {
            unregisterNetworkConnection(connection)
        }

        // Generate new node partitions.
        val partitions = mutableListOf<MutableSet<UUID>>()
        for (connectedNode in connectedNodes) {
            // Skip if the node is already counted.
            if (partitions.any { it.contains(connectedNode.uniqueId) }) {
                continue
            }

            // Create a new group.
            val collected = mutableSetOf(connectedNode.uniqueId)
            collectConnectedNodes(connectedNode, collected)
            partitions.add(collected)
        }

        // Return if all connected nodes are still connected via another path.
        if (partitions.size == 1) {
            return
        }

        // Reassign networks.
        val networks = partitions.map { createNetwork() }
        for (networkNode in node.network.nodes().toList()) {
            networkNode.network = networks[partitions.indexOfFirst { it.contains(networkNode.uniqueId) }]
        }
        unregisterNetwork(node.network)

        eventScope.post(NetworkSplitEvent(node.network, networks))
        node.destroy()
    }

    private fun registerNetworkNode(node: NetworkNodeImpl) {
        node.network.registerNode(node)
        networkNodes[node.uniqueId] = node
    }

    private fun unregisterNetworkNode(node: NetworkNodeImpl) {
        node.network.unregisterNode(node)
        networkNodes.remove(node.uniqueId)
    }

    override fun networkConnections(): Collection<NetworkConnectionImpl> {
        return networkConnections.values
    }

    override fun networkConnection(nodeA: NetworkNode, nodeB: NetworkNode): NetworkConnectionImpl? {
        return networkConnections[networkConnectionIndex(nodeA, nodeB)]
    }

    override fun networkConnection(nodeAUniqueId: UUID, nodeBUniqueId: UUID): NetworkConnectionImpl? {
        return networkConnections[networkConnectionIndex(nodeAUniqueId, nodeBUniqueId)]
    }

    override fun createNetworkConnection(nodeA: NetworkNode, nodeB: NetworkNode): NetworkConnection {
        require(nodeA is NetworkNodeImpl)
        require(nodeB is NetworkNodeImpl)

        // Check if the two nodes are already connected.
        networkConnection(nodeA, nodeB)?.let { return it }

        // Check if the two nodes are in the same network. If not, merge the two networks.
        val networkA = nodeA.network
        val networkB = nodeB.network
        val requiresMerge = nodeA.network != nodeB.network
        val network = if (requiresMerge) {
            mergeNetworks(nodeA.network, nodeB.network)
        } else {
            nodeA.network
        }

        // Connect the nodes with each other.
        val connection = NetworkConnectionImpl(nodeA, nodeB)
        registerNetworkConnection(connection)

        // Publish the event.
        if (requiresMerge) {
            eventScope.post(NetworkMergeEvent(network, listOf(networkA, networkB)))
        }
        eventScope.post(NetworkConnectionCreateEvent(connection))

        // Return the created connection.
        return connection
    }

    override fun removeNetworkConnection(nodeA: NetworkNode, nodeB: NetworkNode) {
        require(nodeA is NetworkNodeImpl)
        require(nodeB is NetworkNodeImpl)

        // Check if the two nodes are not connected.
        val connection = networkConnection(nodeA, nodeB) ?: return

        // Remove connection
        unregisterNetworkConnection(connection)
        eventScope.post(NetworkConnectionDestroyEvent(connection))

        // Return if the nodes are still connected, otherwise the network has to be split.
        if (existsPathBetweenNodes(nodeA, nodeB)) {
            return
        }

        val oldNetwork = nodeA.network

        // Create a new group.
        val networkANodes = mutableSetOf(nodeA.uniqueId)
        val networkBNodes = mutableSetOf(nodeB.uniqueId)
        collectConnectedNodes(nodeA, networkANodes)
        collectConnectedNodes(nodeB, networkBNodes)

        // Reassign networks.
        val networkA = createNetwork()
        val networkB = createNetwork()
        for (networkNode in oldNetwork.nodes().toList()) {
            networkNode.network = when (networkNode.uniqueId) {
                in networkANodes -> networkA
                in networkBNodes -> networkB
                else -> throw IllegalStateException("Connected node is in no node pool")
            }
        }
        unregisterNetwork(oldNetwork)

        // Publish event.
        eventScope.post(NetworkSplitEvent(oldNetwork, listOf(networkA, networkB)))
    }

    override fun areNodesConnected(nodeA: NetworkNode, nodeB: NetworkNode): Boolean {
        return networkConnectionIndex(nodeA, nodeB) in networkConnections
    }

    private fun registerNetworkConnection(connection: NetworkConnectionImpl) {
        networkConnections[connection.index()] = connection
        connection.node1.registerConnection(connection.node2)
        connection.node2.registerConnection(connection.node1)
    }

    private fun unregisterNetworkConnection(connection: NetworkConnectionImpl) {
        networkConnections.remove(connection.index())
        connection.node1.unregisterConnection(connection.node2)
        connection.node2.unregisterConnection(connection.node1)
    }

    private fun networkConnectionIndex(uniqueIdA: UUID, uniqueIdB: UUID): Pair<UUID, UUID> {
        require(uniqueIdA != uniqueIdB) { "Reflective connections are not allowed." }

        return if (uniqueIdA.hashCode() >= uniqueIdB.hashCode()) {
            Pair(uniqueIdA, uniqueIdB)
        } else {
            Pair(uniqueIdB, uniqueIdA)
        }
    }

    private fun networkConnectionIndex(nodeA: NetworkNode, nodeB: NetworkNode): Pair<UUID, UUID> {
        return networkConnectionIndex(nodeA.uniqueId, nodeB.uniqueId)
    }

    fun existsPathBetweenNodes(nodeA: NetworkNode, nodeB: NetworkNode): Boolean {
        require(nodeA is NetworkNodeImpl)
        require(nodeB is NetworkNodeImpl)

        // Check if the two nodes are connected.
        if (areNodesConnected(nodeA, nodeB)) {
            return true
        }

        val visitedNodes = mutableSetOf<UUID>()
        val nodesToVisit = nodeA.connectedNodes().toMutableList()
        val nodesToVisitNext = mutableListOf<NetworkNodeImpl>()
        while (nodesToVisit.isNotEmpty()) {
            for (node in nodesToVisit) {
                // Loop over all nodes that are connected to an already visited node but have not yet been visited themselves.
                for (connectedNode in node.connectedNodes().filter { it.uniqueId !in visitedNodes }) {
                    // Check if the node is the target node.
                    if (node == nodeB) {
                        return true
                    }

                    // Visit all connected nodes in the next iteration
                    nodesToVisitNext += connectedNode
                }
            }

            // Update visited nodes.
            visitedNodes += nodesToVisit.map(NetworkNodeImpl::uniqueId)
            nodesToVisit.clear()
            nodesToVisit += nodesToVisitNext
            nodesToVisitNext.clear()
        }

        // The node has not been reached, therefore no path exists between the two nodes.
        return false
    }

    private fun collectConnectedNodes(networkNode: NetworkNodeImpl, collected: MutableSet<UUID>) {
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

    private fun mergeNetworks(networks: List<NetworkImpl>): NetworkImpl {
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

    private fun mergeNetworks(network1: NetworkImpl, network2: NetworkImpl): NetworkImpl {
        // Check if the two networks are the same.
        if (network1 == network2) {
            return network1
        }

        // Create a new network.
        val network = createNetwork()

        // Add all nodes of the previous two networks to the new network.
        network1.nodes().forEach(network::registerNode)
        network2.nodes().forEach(network::registerNode)

        // Unregister old networks.
        unregisterNetwork(network1)
        unregisterNetwork(network2)

        return network
    }

    private fun mergeNetworksIntoFirst(network1: NetworkImpl, network2: NetworkImpl): NetworkImpl {
        // Check if the two networks are the same.
        if (network1 == network2) {
            return network1
        }

        // Add all nodes of the previous two networks to the new network.
        network2.nodes().forEach(network1::registerNode)

        // Unregister other networks.
        unregisterNetwork(network2)

        return network1
    }
}
