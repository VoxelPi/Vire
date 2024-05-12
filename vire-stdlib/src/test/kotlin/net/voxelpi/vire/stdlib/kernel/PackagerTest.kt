package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.BooleanState
import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.Vire
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.environment.Environment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PackagerTest {

    private lateinit var environment: Environment
    private lateinit var circuit: Circuit

    @BeforeEach
    fun setUp() {
        environment = Vire.createEnvironment(emptyList())
        circuit = environment.createCircuit(Identifier("vire-test", "test"))
    }

    @Test
    fun simplePackager() {
        // Create instances
        val blockCount = 4
        val packager = Packager.createVariant {
            this[Packager.blockCount] = blockCount
            this[Packager.blockSize] = 1
        }.getOrThrow().createInstance().getOrThrow()
        val unpackager = Unpackager.createVariant {
            this[Unpackager.blockCount] = blockCount
            this[Unpackager.blockSize] = 1
        }.getOrThrow().createInstance().getOrThrow()

        val state = BooleanState(booleanArrayOf(false, true, true, true)).logicState()

        val packagerSimulation = environment.createSimulation(packager)
        val unpackagerSimulation = environment.createSimulation(unpackager)

        packagerSimulation.modifyInputs {
            for (index in 0..<blockCount) {
                this[Packager.input, index] = LogicState.value(state[index])
            }
        }
        packagerSimulation.simulateStep()
        val packaged = packager[Packager.output]
        assertEquals(state, packaged)

        unpackagerSimulation.modifyInputs {
            this[Unpackager.input] = packaged
        }
        unpackagerSimulation.simulateStep()
        val unpackaged = unpackager[Unpackager.output]
        for (index in 0..<blockCount) {
            assertEquals(LogicState.value(state[0], 1), unpackaged[0])
        }
    }
}
