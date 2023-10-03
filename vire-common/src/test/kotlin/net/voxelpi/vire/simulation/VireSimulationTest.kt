package net.voxelpi.vire.simulation

import io.github.oshai.kotlinlogging.KotlinLogging
import net.voxelpi.vire.VireImplementation
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
    fun clear() {
        simulation.createNetwork()
        simulation.clear()
        assert(simulation.networks().isEmpty()) { "Not all networks have been deleted." }
    }
}
