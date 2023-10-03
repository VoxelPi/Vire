package net.voxelpi.vire.simulation

import io.github.oshai.kotlinlogging.KotlinLogging
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
        val node1 = network.createNode(emptyList())
        val node2 = network.createNode(emptyList())

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
    }

    @Test
    fun clear() {
        simulation.createNetwork()
        simulation.clear()
        assert(simulation.networks().isEmpty()) { "Not all networks have been deleted." }
    }
}
