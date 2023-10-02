package net.voxelpi.vire.simulation

import net.voxelpi.vire.VireImplementation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class VireSimulationTest {

    private lateinit var simulation: VireSimulation

    @BeforeEach
    fun setUp() {
        simulation = VireSimulation(emptyList())
    }

    @Test
    fun clear() {
        simulation.createNetwork()
        simulation.clear()
        assert(simulation.networks().isEmpty()) { "Not all networks have been deleted." }
    }
}
