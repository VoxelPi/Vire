package net.voxelpi.vire.engine.util

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GraphUtilTest {

    @Test
    fun `test connected graph`() {
        val nodes = setOf(1, 2, 3, 4, 5)
        val connections = setOf(
            1 to 2,
            2 to 3,
            3 to 1,
            3 to 4,
            4 to 5,
        )

        assertTrue(GraphUtil.isConnectedGraph(nodes, connections), "Connected graph was labeled as disconnected graph")
    }

    @Test
    fun `test disconnected graph`() {
        val nodes = setOf(1, 2, 3, 4, 5)
        val connections = setOf(
            1 to 2,
            2 to 3,
            3 to 1,
            4 to 5,
        )

        assertFalse(GraphUtil.isConnectedGraph(nodes, connections), "Disconnected graph was labeled as connected graph")
    }
}
