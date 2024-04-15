package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.input
import net.voxelpi.vire.api.simulation.statemachine.output
import net.voxelpi.vire.engine.VireImplementation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VireSimulationTest {

    private lateinit var simulation: VireSimulation
    private lateinit var circuit: VireCircuit

    @BeforeEach
    fun setUp() {
        simulation = VireImplementation.createSimulation(emptyList())
        circuit = simulation.circuit
    }

    @Test
    fun connect2NodesInSameNetwork() {
        val network = circuit.createNetwork()
        val node0 = network.createNode(emptyList())
        val node1 = network.createNode(listOf(node0))
        val node2 = network.createNode(listOf(node0))

        assert(!circuit.areNodesConnectedDirectly(node1, node2)) { "Nodes are connected before connecting them." }
        circuit.createNetworkNodeConnection(node1, node2)
        assert(circuit.areNodesConnectedDirectly(node1, node2)) { "Nodes are not connected after connecting them." }
    }

    @Test
    fun connect2NodesInDifferentNetworks() {
        val network1 = circuit.createNetwork()
        val node1 = network1.createNode(emptyList())

        val network2 = circuit.createNetwork()
        val node2 = network2.createNode(emptyList())

        circuit.createNetworkNodeConnection(node1, node2)

        assertEquals(1, circuit.networks().size) { "Only one network should exist after the merge." }
        assert(circuit.areNodesConnectedDirectly(node1, node2)) { "Nodes are not connected after the merge." }
    }

    @Test
    fun removeNodeConnectionWithSplit() {
        val network = circuit.createNetwork()
        val nodeA = network.createNode(emptyList())
        val nodeB = network.createNode(listOf(nodeA))

        assertEquals(1, circuit.networks().size)
        assertEquals(2, circuit.networkNodes().size)

        circuit.removeNetworkNodeConnection(nodeA, nodeB)

        assertEquals(2, circuit.networks().size)
        assertEquals(2, circuit.networkNodes().size)
        assert(nodeA.network.uniqueId != nodeB.network.uniqueId)
    }

    @Test
    fun removeNodeFromNetworkWithSplit() {
        val network = circuit.createNetwork()
        val nodeA = network.createNode(emptyList())
        val nodeMiddle = network.createNode(listOf(nodeA))
        val nodeB = network.createNode(listOf(nodeMiddle))

        assertEquals(1, circuit.networks().size)
        assertEquals(3, circuit.networkNodes().size)

        nodeMiddle.remove()

        assertEquals(2, circuit.networks().size)
        assertEquals(2, circuit.networkNodes().size)
        assert(nodeA.network != nodeB.network)
    }

    @Test
    fun clear() {
        circuit.createNetwork()
        circuit.clear()
        assert(circuit.networks().isEmpty()) { "Not all networks have been deleted." }
    }

    @Test
    fun simpleClock() {
        val inputVariable = input("input")
        val outputVariable = output("output")

        val stateMachine = StateMachine.create(Identifier("vire-test", "buffer")) {
            declare(inputVariable)
            declare(outputVariable)

            update = { context ->
                context[outputVariable] = !context[inputVariable].booleanState()
            }
        }

        val component = circuit.createComponent(stateMachine)
        val inputPort = component.createPort(inputVariable.variable())
        val outputPort = component.createPort(outputVariable.variable())
        circuit.createNetworkNodeConnection(inputPort.node, outputPort.node)

        assertEquals(1, circuit.networks().size) { "More than one network remain after connecting the two nodes." }
        val network = circuit.networks().first()

        assertEquals(network, inputPort.network) { "Input port network was not updated." }
        assertEquals(network, outputPort.network) { "Output port network was not updated." }

        network.state = LogicState.value(false)
        simulation.simulateSteps(1)
        assertEquals(LogicState.value(true), network.state) { "Incorrect simulation result." }

        var previousState = network.state.booleanState()
        for (i in 1..100) {
            simulation.simulateSteps(1)
            val state = network.state
            assertEquals(!previousState, state.booleanState()) { "Clock stopped working after $i cycles" }
            previousState = state.booleanState()
        }
    }

    @Test
    fun separateAndJoin() {
        val outputVariable = output("output")
        val outputState = LogicState.value(true, 1)

        val stateMachine = StateMachine.create(Identifier("vire-test", "buffer")) {
            declare(outputVariable)

            update = { context ->
                context[outputVariable] = outputState
            }
        }

        val component = circuit.createComponent(stateMachine)
        val outputPort = component.createPort(outputVariable.variable())

        // Simulate output.
        simulation.simulateSteps(1)

        // Connect another node to the port.
        val node1 = outputPort.node
        val node2 = node1.network.createNode(listOf(node1))
        assertEquals(outputState, node2.network.state)

        // Separate the nodes.
        circuit.removeNetworkNodeConnection(node1, node2)
        assertEquals(outputState, node1.network.state)
        assertEquals(LogicState.EMPTY, node2.network.state)

        // Connect the nodes.
        circuit.createNetworkNodeConnection(node1, node2)
        assertEquals(outputState, node1.network.state)
        assertEquals(outputState, node2.network.state)
    }
}
