package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.BooleanState
import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.Vire
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.logicState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MemoryTest {

    private lateinit var environment: Environment
    private lateinit var circuit: Circuit

    @BeforeEach
    fun setUp() {
        environment = Vire.createEnvironment(emptyList())
        circuit = environment.createCircuit(Identifier("vire-test", "test"))
    }

    @Test
    fun readWriteMemory() {
        val memory = Memory.createVariant {
            this[Memory.readOnly] = false
            this[Memory.wordSize] = 8 // Each address stores one byte
            this[Memory.addressBits] = 8 // 8 address bits (256 different addresses)
        }.getOrThrow().createInstance().getOrThrow()

        val simulation = environment.createSimulation(memory)

        // Check that memory is only written when write active bit is set.
        simulation.modifyInputs {
            this[Memory.writeActive] = logicState(false)
            this[Memory.readAddress] = BooleanState.integer(5)
            this[Memory.writeAddress] = BooleanState.integer(3)
            this[Memory.writeValue] = BooleanState.integer(0x23)
        }
        simulation.simulateStep()
        assertEquals(BooleanState.integer(0, 8), memory[Memory.memory][3])
        assertEquals(LogicState.EMPTY, memory[Memory.readValue])

        // Check that memory can be written.
        simulation.reset()
        simulation.modifyInputs {
            this[Memory.writeActive] = logicState(true)
            this[Memory.readAddress] = BooleanState.integer(5)
            this[Memory.writeAddress] = BooleanState.integer(3)
            this[Memory.writeValue] = BooleanState.integer(0x22)
        }
        simulation.simulateStep()
        assertEquals(BooleanState.integer(0x22, 8), memory[Memory.memory][3])
        assertEquals(LogicState.EMPTY, memory[Memory.readValue])

        // Try reading the value back.
        simulation.reset()
        simulation.modifyInputs {
            this[Memory.writeActive] = logicState(false)
            this[Memory.readActive] = logicState(true)
            this[Memory.writeAddress] = BooleanState.integer(5)
            this[Memory.readAddress] = BooleanState.integer(3)
        }
        simulation.simulateStep()
        assertEquals(0x22, memory[Memory.readValue].booleanState().toInt())
    }
}
