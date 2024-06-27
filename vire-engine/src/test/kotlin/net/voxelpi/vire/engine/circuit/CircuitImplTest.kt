package net.voxelpi.vire.engine.circuit

import net.voxelpi.vire.engine.Vire
import net.voxelpi.vire.engine.environment.Environment
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CircuitImplTest {

    private lateinit var environment: Environment
    private lateinit var circuit: Circuit

    @BeforeEach
    fun setUp() {
        environment = Vire.createEnvironmentImpl(emptyList())
        circuit = environment.createCircuit()
    }

    @Test
    fun `test network connection creation`() {
        // Create first subnetwork.
        val nodeA1 = circuit.createNetworkNode()
        val nodeA2 = circuit.createNetworkNode(nodeA1)
        val networkA = nodeA2.network
        assertEquals(1, networkA.connections().size)

        // Try creating existing connection
        circuit.createNetworkConnection(nodeA1, nodeA2)
        assertEquals(1, networkA.connections().size)

        // Create first subnetwork.
        val nodeB1 = circuit.createNetworkNode()
        val nodeB2 = circuit.createNetworkNode(nodeB1)
        val networkB = nodeB2.network
        assertEquals(1, networkB.connections().size)

        // Merge the two networks by creating a connection.
        val connection = circuit.createNetworkConnection(nodeA1, nodeB1)
        val network = connection.network
        assertEquals(3, network.connections().size)
    }
}
