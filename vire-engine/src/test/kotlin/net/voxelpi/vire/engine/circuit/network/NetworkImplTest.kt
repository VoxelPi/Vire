package net.voxelpi.vire.engine.circuit.network

import net.voxelpi.event.on
import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.Vire
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.event.network.NetworkCreateEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkDestroyEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkNodeCreateEvent
import net.voxelpi.vire.engine.circuit.event.network.NetworkNodeDestroyEvent
import net.voxelpi.vire.engine.environment.Environment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NetworkImplTest {
    private lateinit var environment: Environment
    private lateinit var circuit: Circuit

    @BeforeEach
    fun setUp() {
        environment = Vire.createEnvironmentImpl(emptyList())
        circuit = environment.createCircuit(Identifier("vire-test", "test"))
    }

    @Test
    fun `create and destroy network`() {
        var createCounter = 0
        environment.eventScope.on<NetworkCreateEvent> { createCounter++ }
        var destroyCounter = 0
        environment.eventScope.on<NetworkDestroyEvent> { destroyCounter++ }

        val network = circuit.createNetwork()
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
    fun createDestroyNetworkNode() {
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
}
