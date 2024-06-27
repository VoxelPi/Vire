package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.Vire
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.kernel.circuit.CircuitKernel
import net.voxelpi.vire.engine.kernel.variable.createInput
import net.voxelpi.vire.engine.kernel.variable.createOutput
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CircuitKernelTest {

    private lateinit var environment: Environment

    @BeforeEach
    fun setUp() {
        environment = Vire.createEnvironmentImpl(emptyList())
    }

    @Test
    fun `test circuit kernel`() {
        val circuit = environment.createCircuit()

        val innerKernelInput = createInput("input")
        val innerKernelOutput = createOutput("output") {
            initialization = { LogicState.value(true, 1) }
        }
        val innerKernel = kernel {
            declare(innerKernelInput)
            declare(innerKernelOutput)

            onUpdate { context ->
                context[innerKernelOutput] = context[innerKernelInput].booleanState().not()
            }
        }
        val innerKernelVariant = innerKernel.createVariant().getOrThrow()

        val component = circuit.createComponent(innerKernelVariant)
        val portIn = component.createPort(innerKernelInput)
        val portOut = component.createPort(innerKernelOutput)
        val connection = circuit.createNetworkConnection(portIn.networkNode, portOut.networkNode)

        val kernelVariant = circuit.createKernelVariant()
        val kernelInstance = kernelVariant.createInstance().getOrThrow()

        val simulation = environment.createSimulation(kernelInstance)
        val initialCircuitState = simulation.state[CircuitKernel.CIRCUIT_STATE]
        val initialNetworkState = initialCircuitState[connection.network]
        assertEquals(LogicState.value(true, 1), initialNetworkState)

        for (step in 1..10) {
            simulation.simulateStep()
            val circuitState = simulation.state[CircuitKernel.CIRCUIT_STATE]
            val networkState = circuitState[connection.network]
            assertEquals(LogicState.value(step % 2 == 0, 1), networkState)
        }
    }
}
