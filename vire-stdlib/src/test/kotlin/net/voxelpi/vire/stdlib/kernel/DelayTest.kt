package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.Vire
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.environment.Environment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DelayTest {

    private lateinit var environment: Environment
    private lateinit var circuit: Circuit

    @BeforeEach
    fun setUp() {
        environment = Vire.createEnvironment(emptyList())
        circuit = environment.createCircuit()
    }

    @Test
    fun `test simple delay`() {
        val delay = Delay.createVariant().getOrThrow().createInstance {
            this[Delay.delay] = 4
        }.getOrThrow()

        val simulation = environment.createSimulation(delay)

        simulation.modifyInputs {
            this[Delay.input] = LogicState.value(true, 1)
        }
        simulation.simulateStep()
        assertEquals(LogicState.EMPTY, simulation.state[Delay.output])

        simulation.modifyInputs {
            this[Delay.input] = LogicState.value(true, 2)
        }
        simulation.simulateStep()
        assertEquals(LogicState.EMPTY, simulation.state[Delay.output])

        simulation.modifyInputs {
            this[Delay.input] = LogicState.value(true, 3)
        }
        simulation.simulateStep()
        assertEquals(LogicState.EMPTY, simulation.state[Delay.output])

        simulation.modifyInputs {
            this[Delay.input] = LogicState.value(true, 4)
        }
        simulation.simulateStep()
        assertEquals(LogicState.value(true, 1), simulation.state[Delay.output])

        simulation.modifyInputs {
            this[Delay.input] = LogicState.value(true, 5)
        }
        simulation.simulateStep()
        assertEquals(LogicState.value(true, 2), simulation.state[Delay.output])

        simulation.modifyInputs {
            this[Delay.input] = LogicState.value(true, 6)
        }
        simulation.simulateStep()
        assertEquals(LogicState.value(true, 3), simulation.state[Delay.output])
    }
}
