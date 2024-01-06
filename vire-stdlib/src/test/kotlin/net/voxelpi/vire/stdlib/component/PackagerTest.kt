package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.simulation.BooleanState
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.engine.VireImplementation
import net.voxelpi.vire.engine.simulation.VireSimulation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PackagerTest {

    private lateinit var simulation: VireSimulation

    @BeforeEach
    fun setUp() {
        simulation = VireImplementation.createSimulation(emptyList())
    }

    @Test
    fun simplePackager() {
        // Create instances
        val blockCount = 4
        val packager = simulation.createStateMachineInstance(Packager) {
            this[Packager.blockCount] = blockCount
            this[Packager.blockSize] = 1
        }
        val unpackager = simulation.createStateMachineInstance(Unpackager) {
            this[Unpackager.blockCount] = blockCount
            this[Unpackager.blockSize] = 1
        }

        val state = BooleanState(booleanArrayOf(false, true, true, true)).logicState()

        for (index in 0..<blockCount) {
            packager[Packager.input, index] = LogicState.value(state[index])
        }
        packager.update()
        val packaged = packager[Packager.output, 0]
        assertEquals(state, packaged)

        unpackager[Unpackager.input] = packaged
        unpackager.update()
        val unpackaged = unpackager.vector(Unpackager.output)
        for (index in 0..<blockCount) {
            assertEquals(LogicState.value(state[0], 1), unpackaged[0])
        }
    }
}
