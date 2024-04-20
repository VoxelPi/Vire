package net.voxelpi.vire.engine.circuit

import net.voxelpi.event.EventScope
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.circuit.component.Component
import net.voxelpi.vire.engine.circuit.component.ComponentImpl
import net.voxelpi.vire.engine.circuit.kernel.Kernel
import net.voxelpi.vire.engine.circuit.kernel.variable.KernelConfiguration
import net.voxelpi.vire.engine.circuit.network.Network
import net.voxelpi.vire.engine.circuit.network.NetworkConnection
import net.voxelpi.vire.engine.circuit.network.NetworkConnectionImpl
import net.voxelpi.vire.engine.circuit.network.NetworkImpl
import net.voxelpi.vire.engine.circuit.network.NetworkNode
import net.voxelpi.vire.engine.circuit.network.NetworkNodeImpl
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.environment.EnvironmentImpl
import net.voxelpi.vire.engine.simulation.Simulation
import net.voxelpi.vire.engine.simulation.SimulationImpl
import java.util.UUID

/**
 * A logic circuit created by linking different components together.
 */
public interface Circuit {

    /**
     * The environment of the circuit.
     */
    public val environment: Environment

    /**
     * The event scope of the environment.
     */
    public val eventScope: EventScope

    /**
     * Creates a new simulation of this circuit.
     */
    public fun createSimulation(): Simulation

    /**
     * Returns a collection of all registered components.
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
    public fun createComponent(kernel: Kernel, configuration: KernelConfiguration): Component

    /**
     * Removes the given [component] from the circuit.
     */
    public fun removeComponent(component: Component)

    /**
     * Returns a collection of all registered networks.
     */
    public fun networks(): Collection<Network>

    /**
     * Returns the network with the given [uniqueId].
     */
    public fun network(uniqueId: UUID): Network?

    /**
     * Creates a new network.
     */
    public fun createNetwork(initialization: LogicState = LogicState.EMPTY, uniqueId: UUID = UUID.randomUUID()): Network

    /**
     * Removes the given [network] and all its nodes from the simulation.
     */
    public fun removeNetwork(network: Network) {}

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
}

internal class CircuitImpl(
    override val environment: EnvironmentImpl,
) : Circuit {

    override val eventScope: EventScope = environment.eventScope.createSubScope()

    private val components: MutableMap<UUID, ComponentImpl> = mutableMapOf()
    private val networks: MutableMap<UUID, NetworkImpl> = mutableMapOf()
    private val networkNodes: MutableMap<UUID, NetworkNodeImpl> = mutableMapOf()
    private val networkConnections: MutableMap<Pair<UUID, UUID>, NetworkConnectionImpl> = mutableMapOf()

    override fun createSimulation(): Simulation {
        return SimulationImpl(this)
    }

    override fun components(): Collection<ComponentImpl> {
        return components.values
    }

    override fun component(uniqueId: UUID): ComponentImpl? {
        return components[uniqueId]
    }

    override fun createComponent(kernel: Kernel, configuration: KernelConfiguration): ComponentImpl {
        TODO("Not yet implemented")
    }

    override fun removeComponent(component: Component) {
        TODO("Not yet implemented")
    }

    override fun networks(): Collection<NetworkImpl> {
        return networks.values
    }

    override fun network(uniqueId: UUID): NetworkImpl? {
        return networks[uniqueId]
    }

    override fun createNetwork(initialization: LogicState, uniqueId: UUID): NetworkImpl {
        TODO("Not yet implemented")
    }

    override fun networkNodes(): Collection<NetworkNodeImpl> {
        return networkNodes.values
    }

    override fun networkNode(uniqueId: UUID): NetworkNodeImpl? {
        return networkNodes[uniqueId]
    }

    override fun createNetworkNode(network: Network, uniqueId: UUID): NetworkNodeImpl {
        TODO("Not yet implemented")
    }

    override fun createNetworkNode(connectedTo: Collection<NetworkNode>, uniqueId: UUID): NetworkNodeImpl {
        TODO("Not yet implemented")
    }

    override fun removeNetworkNode(node: NetworkNode) {
        TODO("Not yet implemented")
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
}
