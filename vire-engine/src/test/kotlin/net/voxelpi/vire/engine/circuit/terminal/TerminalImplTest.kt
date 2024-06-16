package net.voxelpi.vire.engine.circuit.terminal

import net.voxelpi.event.on
import net.voxelpi.vire.engine.Vire
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.event.terminal.TerminalCreateEvent
import net.voxelpi.vire.engine.circuit.event.terminal.TerminalDestroyEvent
import net.voxelpi.vire.engine.circuit.event.terminal.TerminalSelectVariableEvent
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.kernel.variable.createInput
import net.voxelpi.vire.engine.kernel.variable.createOutput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class TerminalImplTest {
    private lateinit var environment: Environment
    private lateinit var circuit: Circuit

    @BeforeEach
    fun setUp() {
        environment = Vire.createEnvironmentImpl(emptyList())
        circuit = environment.createCircuit()
    }

    @Test
    fun `create and destroy terminal`() {
        var createCounter = 0
        environment.eventScope.on<TerminalCreateEvent> { createCounter++ }
        var destroyCounter = 0
        environment.eventScope.on<TerminalDestroyEvent> { destroyCounter++ }

        val uniqueId1 = UUID.randomUUID()
        val terminal1 = circuit.createTerminal(null, uniqueId = uniqueId1)
        val terminal2 = circuit.createTerminal(null)
        assertContentEquals(setOf(terminal1, terminal2), circuit.terminals())
        assertEquals(2, createCounter)
        assertEquals(0, destroyCounter)

        assertEquals(terminal1, circuit.terminal(uniqueId1))

        createCounter = 0
        destroyCounter = 0
        terminal2.remove()
        assertContentEquals(setOf(terminal1), circuit.terminals())
        assertEquals(terminal1, circuit.terminal(uniqueId1))
        assertEquals(0, createCounter)
        assertEquals(1, destroyCounter)

        createCounter = 0
        destroyCounter = 0
        terminal1.remove()
        assertContentEquals(emptySet(), circuit.terminals())
        assertNull(circuit.terminal(uniqueId1))
        assertEquals(0, createCounter)
        assertEquals(1, destroyCounter)
    }

    @Test
    fun `terminal variables`() {
        var eventCounter = 0
        environment.eventScope.on<TerminalSelectVariableEvent> { eventCounter++ }

        val input1 = createInput("input1")
        val input2 = createInput("input2", 2)
        val output = createOutput("output1", 3)

        circuit.declareVariable(input1)
        circuit.declareVariable(input2)
        circuit.declareVariable(output)

        val terminal1 = circuit.createTerminal(input1)
        val terminal2 = circuit.createTerminal(null)
        val terminal3 = circuit.createTerminal(output[1])
        assertEquals(0, eventCounter)

        assertTrue(terminal1.isInput)
        assertFalse(terminal1.isOutput)

        assertFalse(terminal2.isInput)
        assertFalse(terminal2.isOutput)

        assertFalse(terminal3.isInput)
        assertTrue(terminal3.isOutput)

        eventCounter = 0
        terminal2.variable = input2[0]
        assertEquals(1, eventCounter)

        eventCounter = 0
        terminal1.variable = null
        assertEquals(1, eventCounter)
    }
}
