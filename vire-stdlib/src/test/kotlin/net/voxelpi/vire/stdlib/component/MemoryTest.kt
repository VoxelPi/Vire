package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.simulation.BooleanState
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.logicState
import net.voxelpi.vire.engine.VireImplementation
import net.voxelpi.vire.engine.simulation.VireSimulation
import net.voxelpi.vire.engine.simulation.statemachine.VireStateMachineInstance
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MemoryTest {

    private lateinit var simulation: VireSimulation

    @BeforeEach
    fun setUp() {
        simulation = VireImplementation.createSimulation(emptyList())
    }

    @Test
    fun readWriteMemory() {
        val memory = Memory.createInstance {
            this[Memory.readOnly] = false
            this[Memory.wordSize] = 8 // Each address stores one byte
            this[Memory.addressBits] = 8 // 8 address bits (256 different addresses)
        } as VireStateMachineInstance

        // Check that memory is only written when write active bit is set.
        memory[Memory.writeActive] = logicState(false)
        memory[Memory.readAddress] = BooleanState.integer(5)
        memory[Memory.writeAddress] = BooleanState.integer(3)
        memory[Memory.writeValue] = BooleanState.integer(0x23)
        memory.update()
        assertEquals(BooleanState.integer(0, 8), memory[Memory.memory][3])
        assertEquals(LogicState.EMPTY, memory[Memory.readValue])

        // Check that memory can be written.
        memory[Memory.writeActive] = logicState(true)
        memory[Memory.readAddress] = BooleanState.integer(5)
        memory[Memory.writeAddress] = BooleanState.integer(3)
        memory[Memory.writeValue] = BooleanState.integer(0x22)
        memory.update()
        assertEquals(BooleanState.integer(0x22, 8), memory[Memory.memory][3])
        assertEquals(LogicState.EMPTY, memory[Memory.readValue])

        // Try reading the value back.
        memory[Memory.writeActive] = logicState(false)
        memory[Memory.readActive] = logicState(true)
        memory[Memory.writeAddress] = BooleanState.integer(5)
        memory[Memory.readAddress] = BooleanState.integer(3)
        memory.update()
        assertEquals(0x22, memory[Memory.readValue].booleanState().toInt())
    }
}
