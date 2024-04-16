package net.voxelpi.vire.engine.circuit.network

import net.voxelpi.event.on
import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.circuit.event.network.NetworkCreateEvent
import net.voxelpi.vire.api.circuit.event.network.NetworkDestroyEvent
import net.voxelpi.vire.api.circuit.event.network.node.NetworkNodeCreateEvent
import net.voxelpi.vire.api.circuit.event.network.node.NetworkNodeDestroyEvent
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.input
import net.voxelpi.vire.api.circuit.statemachine.output
import net.voxelpi.vire.engine.VireImplementation
import net.voxelpi.vire.engine.circuit.VireCircuit
import net.voxelpi.vire.engine.environment.VireEnvironment
import net.voxelpi.vire.engine.simulation.VireSimulation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertEquals

class VireNetworkTest {

    private lateinit var environment: VireEnvironment
    private lateinit var circuit: VireCircuit
    private lateinit var simulation: VireSimulation

    @BeforeEach
    fun setUp() {
        environment = VireImplementation.createEnvironment(emptyList())
        circuit = environment.createCircuit()
        simulation = environment.createSimulation(circuit)
    }

    @Test
    fun contains() {
        val networkA = circuit.createNetwork()
        val networkB = circuit.createNetwork()

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
        val networkA = circuit.createNetwork()
        val networkB = circuit.createNetwork()

        networkA.createNode(emptyList())
        networkA.createNode(networkA.nodes())
        networkB.createNode(emptyList())

        assertEquals(2, networkA.nodes().size)
        assertEquals(1, networkB.nodes().size)
    }

    @Test
    fun createNode() {
        val networkA = circuit.createNetwork()
        val networkB = circuit.createNetwork()

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
        val inputVariable = input("input")
        val outputVariable = output("output")

        val stateMachine = StateMachine.create(Identifier("vire-test", "buffer")) {
            declare(inputVariable)
            declare(outputVariable)

            update = { context ->
                context[outputVariable] = context[inputVariable]
            }
        }

        val component = circuit.createComponent(stateMachine)
        val inputPort = component.createPort(inputVariable.variable())
        val outputPort = component.createPort(outputVariable.variable())

        assertEquals(1, inputPort.network.ports().size)
        assertEquals(1, outputPort.network.ports().size)

        circuit.createNetworkNodeConnection(inputPort.node, outputPort.node)
        assertEquals(2, circuit.networks().first().ports().size)
    }

    @Test
    fun createDestroyNetwork() {
        var createCounter = 0
        var destroyCounter = 0

        environment.eventScope.on<NetworkCreateEvent> {
            createCounter++
        }

        environment.eventScope.on<NetworkDestroyEvent> {
            destroyCounter++
        }

        val network = circuit.createNetwork()
        network.remove()
        network.remove() // Try removing the network twice.

        assertEquals(1, createCounter)
        assertEquals(1, destroyCounter)
    }

    @Test
    fun createDestroyNetworkNode() {
        var createCounter = 0
        var destroyCounter = 0

        environment.eventScope.on<NetworkNodeCreateEvent> {
            createCounter++
        }

        environment.eventScope.on<NetworkNodeDestroyEvent> {
            destroyCounter++
        }

        val node = circuit.createNetworkNode()
        node.remove()
        node.remove() // Try removing the network node twice.

        assertEquals(1, createCounter)
        assertEquals(1, destroyCounter)
    }
}
