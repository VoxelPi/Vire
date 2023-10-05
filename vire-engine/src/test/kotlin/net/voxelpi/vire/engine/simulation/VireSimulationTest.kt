package net.voxelpi.vire.engine.simulation

import io.github.oshai.kotlinlogging.KotlinLogging
import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.api.simulation.component.StateMachineInput
import net.voxelpi.vire.api.simulation.component.StateMachineOutput
import net.voxelpi.vire.api.simulation.network.NetworkState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class VireSimulationTest {

    private lateinit var simulation: VireSimulation

    private val logger = KotlinLogging.logger {}

    @BeforeEach
    fun setUp() {
        simulation = VireSimulation(emptyList())
        logger.info { "Setup test simulation" }
    }

    @Test
    fun connect2NodesInSameNetwork() {
        val network = simulation.createNetwork()
        val node0 = network.createNode(emptyList())
        val node1 = network.createNode(listOf(node0))
        val node2 = network.createNode(listOf(node0))

        assert(!simulation.areNodesConnectedDirectly(node1, node2)) { "Nodes are connected before connecting them." }
        simulation.createNetworkNodeConnection(node1, node2)
        assert(simulation.areNodesConnectedDirectly(node1, node2)) { "Nodes are not connected after connecting them." }
    }

    @Test
    fun connect2NodesInDifferentNetworks() {
        val network1 = simulation.createNetwork()
        val node1 = network1.createNode(emptyList())

        val network2 = simulation.createNetwork()
        val node2 = network2.createNode(emptyList())

        simulation.createNetworkNodeConnection(node1, node2)

        assertEquals(1, simulation.networks().size) { "Only one network should exist after the merge." }
        assert(simulation.areNodesConnectedDirectly(node1, node2)) { "Nodes are not connected after the merge." }
    }

    @Test
    fun removeNodeConnectionWithSplit() {
        val network = simulation.createNetwork()
        val nodeA = network.createNode(emptyList())
        val nodeB = network.createNode(listOf(nodeA))

        assertEquals(1, simulation.networks().size)
        assertEquals(2, simulation.networkNodes().size)

        simulation.removeNetworkNodeConnection(nodeA, nodeB)

        assertEquals(2, simulation.networks().size)
        assertEquals(2, simulation.networkNodes().size)
        assert(nodeA.network.uniqueId != nodeB.network.uniqueId)
    }

    @Test
    fun removeNodeFromNetworkWithSplit() {
        val network = simulation.createNetwork()
        val nodeA = network.createNode(emptyList())
        val nodeMiddle = network.createNode(listOf(nodeA))
        val nodeB = network.createNode(listOf(nodeMiddle))

        assertEquals(1, simulation.networks().size)
        assertEquals(3, simulation.networkNodes().size)

        nodeMiddle.remove()

        assertEquals(2, simulation.networks().size)
        assertEquals(2, simulation.networkNodes().size)
        assert(nodeA.network != nodeB.network)
    }

    @Test
    fun clear() {
        simulation.createNetwork()
        simulation.clear()
        assert(simulation.networks().isEmpty()) { "Not all networks have been deleted." }
    }

    @Test
    fun simpleClock() {
        val inputVariable = StateMachineInput("input")
        val outputVariable = StateMachineOutput("output")

        val stateMachine = object : StateMachine(Identifier("vire-test", "buffer")) {
            init {
                declare(inputVariable)
                declare(outputVariable)
            }

            override fun tick(context: StateMachineContext) {
                context[outputVariable] = !context[inputVariable]
            }
        }

        val component = simulation.createComponent(stateMachine)
        val inputPort = component.createPort(inputVariable.createView())
        val outputPort = component.createPort(outputVariable.createView())
        simulation.createNetworkNodeConnection(inputPort.node, outputPort.node)

        assertEquals(1, simulation.networks().size) { "More than one network remain after connecting the two nodes." }
        val network = simulation.networks().first()

        assertEquals(network, inputPort.network) { "Input port network was not updated." }
        assertEquals(network, outputPort.network) { "Output port network was not updated." }

        network.state = NetworkState.value(false)
        simulation.simulateSteps(1)
        assertEquals(NetworkState.value(true), network.state) { "Incorrect simulation result." }
    }
}
