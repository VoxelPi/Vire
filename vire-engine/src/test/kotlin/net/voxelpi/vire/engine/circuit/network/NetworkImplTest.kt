package net.voxelpi.vire.engine.circuit.network

import net.voxelpi.event.on
import net.voxelpi.vire.engine.Vire
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.event.network.NetworkCreateEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkDestroyEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkNodeCreateEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkNodeDestroyEvent
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.kernel.kernel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NetworkImplTest {
    private lateinit var environment: Environment
    private lateinit var circuit: Circuit

    @BeforeEach
    fun setUp() {
        environment = Vire.createEnvironmentImpl(emptyList())
        circuit = environment.createCircuit()
    }

    @Test
    fun `create and destroy network`() {
        var createCounter = 0
        environment.eventScope.on<NetworkCreateEvent> { createCounter++ }
        var destroyCounter = 0
        environment.eventScope.on<NetworkDestroyEvent> { destroyCounter++ }

        val uniqueId = UUID.randomUUID()
        val network = circuit.createNetwork(uniqueId = uniqueId)
        assertEquals(network, circuit.network(uniqueId))
        network.remove()

        // Try removing the network again.
        assertThrows<Exception> { network.remove() }

        assertEquals(1, createCounter)
        assertEquals(1, destroyCounter)
    }

    @Test
    fun `network contains node check`() {
        val node1 = circuit.createNetworkNode()
        val node2 = circuit.createNetworkNode(node1)
        val networkA = node1.network

        val node3 = circuit.createNetworkNode()
        val networkB = node3.network

        assertEquals(2, networkA.nodes().size)
        assertEquals(1, networkB.nodes().size)

        assert(node1 in networkA)
        assert(node1 !in networkB)
        assert(node2 in networkA)
        assert(node2 !in networkB)
        assert(node3 !in networkA)
        assert(node3 in networkB)
    }

    @Test
    fun `network node list`() {
        val networkA = circuit.createNetwork()
        val networkB = circuit.createNetwork()

        val nodeA1 = circuit.createNetworkNode(networkA)
        val nodeA2 = circuit.createNetworkNode(nodeA1)
        val nodeB1 = circuit.createNetworkNode(networkB)

        assertContentEquals(setOf(nodeA1, nodeA2), networkA.nodes())
        assertContentEquals(setOf(nodeB1), networkB.nodes())
    }

    @Test
    fun `create and destroy network node`() {
        var createCounter = 0
        environment.eventScope.on<NetworkNodeCreateEvent> { createCounter++ }
        var destroyCounter = 0
        environment.eventScope.on<NetworkNodeDestroyEvent> { destroyCounter++ }

        val node = circuit.createNetworkNode()
        val network = node.network
        node.remove()
        assertTrue(node !in network)

        // Try removing the network node again.
        assertThrows<Exception> { node.remove() }

        assertEquals(1, createCounter)
        assertEquals(1, destroyCounter)
    }

    @Test
    fun `terminal nodes`() {
        var createCounter = 0
        environment.eventScope.on<NetworkNodeCreateEvent> { createCounter++ }
        var destroyCounter = 0
        environment.eventScope.on<NetworkNodeDestroyEvent> { destroyCounter++ }

        val terminal1 = circuit.createTerminal(null)
        val terminal2 = circuit.createTerminal(null)
        circuit.createNetworkConnection(terminal1.networkNode, terminal2.networkNode)
        assertTrue(terminal1.networkNode.isConnectedTo(terminal2.networkNode))
        val network = terminal1.network

        assertContentEquals(setOf(terminal1, terminal2), network.terminals())
        assertEquals(1, circuit.networks().size)
        assertEquals(1, circuit.networkConnections().size)
        assertEquals(2, createCounter)
        assertEquals(0, destroyCounter)

        createCounter = 0
        destroyCounter = 0
        terminal2.remove()
        assertEquals(1, circuit.networks().size)
        assertEquals(0, circuit.networkConnections().size)
        assertEquals(0, createCounter)
        assertEquals(1, destroyCounter)
    }

    @Test
    fun `port nodes`() {
        var createCounter = 0
        environment.eventScope.on<NetworkNodeCreateEvent> { createCounter++ }
        var destroyCounter = 0
        environment.eventScope.on<NetworkNodeDestroyEvent> { destroyCounter++ }

        val kernel = kernel {}
        val kernelVariant = kernel.createVariant().getOrThrow()
        val component1 = circuit.createComponent(kernelVariant)
        val component2 = circuit.createComponent(kernelVariant)

        val port11 = component1.createPort(null)
        val port12 = component1.createPort(null)
        val port21 = component2.createPort(null)
        circuit.createNetworkConnection(port11.networkNode, port21.networkNode)

        assertContentEquals(setOf(port11, port21), port11.network.componentPorts())
        assertContentEquals(setOf(port12), port12.network.componentPorts())
        assertEquals(3, createCounter)
        assertEquals(0, destroyCounter)

        createCounter = 0
        destroyCounter = 0
        component2.remove()
        assertContentEquals(setOf(port11), port11.network.componentPorts())
        assertContentEquals(setOf(port12), port12.network.componentPorts())
        assertEquals(0, createCounter)
        assertEquals(1, destroyCounter)

        createCounter = 0
        destroyCounter = 0
        port12.remove()
        assertContentEquals(setOf(port11), port11.network.componentPorts())
        assertEquals(1, circuit.networks().size)
        assertEquals(0, createCounter)
        assertEquals(1, destroyCounter)
    }
}
