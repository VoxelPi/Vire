package net.voxelpi.vire.engine.simulation.network

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.api.simulation.component.StateMachineInput
import net.voxelpi.vire.api.simulation.component.StateMachineOutput
import net.voxelpi.vire.api.simulation.event.simulation.network.NetworkCreateEvent
import net.voxelpi.vire.api.simulation.event.simulation.network.NetworkDestroyEvent
import net.voxelpi.vire.api.simulation.event.simulation.network.node.NetworkNodeCreateEvent
import net.voxelpi.vire.api.simulation.event.simulation.network.node.NetworkNodeDestroyEvent
import net.voxelpi.vire.api.simulation.on
import net.voxelpi.vire.engine.simulation.VireSimulation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertEquals

class VireNetworkTest {

    private lateinit var simulation: VireSimulation

    @BeforeEach
    fun setUp() {
        simulation = VireSimulation(emptyList())
    }

    @Test
    fun contains() {
        val networkA = simulation.createNetwork()
        val networkB = simulation.createNetwork()

        val node1 = networkA.createNode(emptyList())
        val node2 = networkA.createNode(listOf(node1))
        val node3 = networkB.createNode(emptyList())

        assert(node1 in networkA)
        assert(node2 in networkA)
        assert(node3 !in networkA)
        assert(node1 !in networkB)
        assert(node2 !in networkB)
        assert(node3 in networkB)
    }

    @Test
    fun nodes() {
        val networkA = simulation.createNetwork()
        val networkB = simulation.createNetwork()

        networkA.createNode(emptyList())
        networkA.createNode(networkA.nodes())
        networkB.createNode(emptyList())

        assertEquals(2, networkA.nodes().size)
        assertEquals(1, networkB.nodes().size)
    }

    @Test
    fun createNode() {
        val networkA = simulation.createNetwork()
        val networkB = simulation.createNetwork()

        val uniqueId = UUID.randomUUID()
        val node = networkA.createNode(emptyList(), uniqueId)
        assertEquals(uniqueId, node.uniqueId)
        assertEquals(1, networkA.nodes().size)
        assertEquals(0, networkB.nodes().size)

        networkA.createNode(listOf(node))
        assertEquals(2, networkA.nodes().size)

        assertThrows<IllegalArgumentException> { networkA.createNode(emptyList()) }
    }

    @Test
    fun ports() {
        val inputVariable = StateMachineInput("input")
        val outputVariable = StateMachineOutput("output")

        val stateMachine = object : StateMachine(Identifier("vire-test", "buffer")) {
            init {
                declare(inputVariable)
                declare(outputVariable)
            }

            override fun tick(context: StateMachineContext) {
                context[outputVariable] = context[inputVariable]
            }
        }

        val component = simulation.createComponent(stateMachine)
        val inputPort = component.createPort(inputVariable.createView())
        val outputPort = component.createPort(outputVariable.createView())

        assertEquals(1, inputPort.network.ports().size)
        assertEquals(1, outputPort.network.ports().size)

        simulation.createNetworkNodeConnection(inputPort.node, outputPort.node)
        assertEquals(2, simulation.networks().first().ports().size)
    }

    @Test
    fun createDestroyNetwork() {
        var createCounter = 0
        var destroyCounter = 0

        simulation.on<NetworkCreateEvent> {
            createCounter++
        }

        simulation.on<NetworkDestroyEvent> {
            destroyCounter++
        }

        val network = simulation.createNetwork()
        network.remove()
        network.remove() // Try removing the network twice.

        // Wait for events to finish.
        simulation.shutdown()

        Assertions.assertEquals(1, createCounter)
        Assertions.assertEquals(1, destroyCounter)
    }

    @Test
    fun createDestroyNetworkNode() {
        var createCounter = 0
        var destroyCounter = 0

        simulation.on<NetworkNodeCreateEvent> {
            createCounter++
        }

        simulation.on<NetworkNodeDestroyEvent> {
            destroyCounter++
        }

        val node = simulation.createNetworkNode()
        node.remove()
        node.remove() // Try removing the network node twice.

        // Wait for events to finish.
        simulation.shutdown()

        Assertions.assertEquals(1, createCounter)
        Assertions.assertEquals(1, destroyCounter)
    }
}
