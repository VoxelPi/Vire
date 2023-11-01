package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.api.simulation.component.input
import net.voxelpi.vire.api.simulation.component.output
import net.voxelpi.vire.api.simulation.network.NetworkState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VireSimulationTest {

    private lateinit var simulation: VireSimulation

    @BeforeEach
    fun setUp() {
        simulation = VireSimulation(emptyList())
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
        val inputVariable = input("input")
        val outputVariable = output("output")

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

        var previousState = network.state
        for (i in 1..100) {
            simulation.simulateSteps(1)
            val state = network.state
            assertEquals(!previousState, state) { "Clock stopped working after $i cycles" }
            previousState = state
        }
    }

    @Test
    fun separateAndJoin() {
        val outputVariable = output("output")
        val outputState = NetworkState.value(true, 1)

        val stateMachine = object : StateMachine(Identifier("vire-test", "buffer")) {
            init {
                declare(outputVariable)
            }

            override fun tick(context: StateMachineContext) {
                context[outputVariable] = outputState
            }
        }

        val component = simulation.createComponent(stateMachine)
        val outputPort = component.createPort(outputVariable.createView())

        // Simulate output.
        simulation.simulateSteps(1)

        // Connect another node to the port.
        val node1 = outputPort.node
        val node2 = node1.network.createNode(listOf(node1))
        assertEquals(outputState, node2.network.state)

        // Separate the nodes.
        simulation.removeNetworkNodeConnection(node1, node2)
        assertEquals(outputState, node1.network.state)
        assertEquals(NetworkState.None, node2.network.state)

        // Connect the nodes.
        simulation.createNetworkNodeConnection(node1, node2)
        assertEquals(outputState, node1.network.state)
        assertEquals(outputState, node2.network.state)
    }
}
