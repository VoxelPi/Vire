package net.voxelpi.vire.serialization

import net.voxelpi.vire.engine.Vire
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.component.ComponentConfiguration
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.patch.SettingStatePatch
import net.voxelpi.vire.stdlib.VireStandardLibrary
import net.voxelpi.vire.stdlib.kernel.BufferGate
import net.voxelpi.vire.stdlib.kernel.Memory
import net.voxelpi.vire.stdlib.kernel.NotGate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class VireSerializationTest {

    private lateinit var environment: Environment
    private lateinit var circuit: Circuit

    @BeforeEach
    fun setUp() {
        environment = Vire.createEnvironment(listOf(VireStandardLibrary))
        circuit = environment.createCircuit()
    }

    @Test
    fun `simple test`() {
        val component1 = circuit.createComponent(VireStandardLibrary.BUFFER_GATE_KERNEL.createVariant().getOrThrow())
        val port11 = component1.createPort(BufferGate.input)
        val port12 = component1.createPort(BufferGate.output)

        val component2 = circuit.createComponent(VireStandardLibrary.NOT_GATE_KERNEL.createVariant().getOrThrow())
        val port21 = component2.createPort(NotGate.input)
        val port22 = component2.createPort(NotGate.output)

        circuit.createNetworkConnection(port12.networkNode, port21.networkNode)
        circuit.createNetworkConnection(port11.networkNode, port22.networkNode)

        // Serialize the circuit
        val serializedCircuit = VireSerialization.serialize(environment, circuit, true)

        // Deserialize the circuit
        val circuit2 = VireSerialization.deserialize(environment, serializedCircuit).getOrThrow()

        assertEquals(circuit.components().size, circuit2.components().size)
        assertEquals(circuit.networks().size, circuit2.networks().size)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `complex component`() {
        val kernelVariant = VireStandardLibrary.MEMORY_KERNEL.createVariant {
            this[Memory.readOnly] = true
        }.getOrThrow()

        val component = circuit.createComponent(kernelVariant)
        val port1 = component.createPort(Memory.readAddress)
        val port2 = component.createPort(Memory.readValue)
        val port3 = component.createPort(Memory.readActive)

        val circuitReadAddress = circuit.declareVariable(Memory.readAddress)
        val circuitReadValue = circuit.declareVariable(Memory.readValue)
        val circuitReadActive = circuit.declareVariable(Memory.readActive)
        val terminal1 = circuit.createTerminal(circuitReadAddress)
        val terminal2 = circuit.createTerminal(circuitReadValue)
        val terminal3 = circuit.createTerminal(circuitReadActive)

        circuit.createNetworkConnection(port1.networkNode, terminal1.networkNode)
        circuit.createNetworkConnection(port2.networkNode, terminal2.networkNode)
        circuit.createNetworkConnection(port3.networkNode, terminal3.networkNode)

        // Serialize the circuit
        val serializedCircuit = VireSerialization.serialize(environment, circuit, true)
//        print(serializedCircuit)

        // Deserialize the circuit
        val circuit2 = VireSerialization.deserialize(environment, serializedCircuit).getOrThrow()

        assertEquals(circuit.components().size, circuit2.components().size)
        assertEquals(circuit.networks().size, circuit2.networks().size)

        val component2 = circuit2.components().first()
        assertEquals(component.kernel, component2.kernel)
        for (parameter in component.kernel.parameters()) {
            assertEquals(
                component.kernelVariant[parameter],
                component2.kernelVariant[parameter],
                "Invalid state of parameter ${parameter.name}",
            )
        }

        // Create the instance of the component kernel variant.
        val settingStates = kernelVariant.settings().associate { setting ->
            val value = component.configuration[setting]
            setting.name to when (value) {
                is ComponentConfiguration.Entry.CircuitSetting -> throw UnsupportedOperationException()
                is ComponentConfiguration.Entry.Value -> value.value
            }
        }
        val kernelInstance = kernelVariant.createInstance(SettingStatePatch(kernelVariant, settingStates)).getOrThrow()
        val kernelState = kernelInstance.initialKernelState()

        val serializedKernelState = VireSerialization.serialize(kernelState, true)
//        println(serializedKernelState)

        val kernelState2 = VireSerialization.deserialize(kernelInstance, serializedKernelState).getOrThrow()
        for (field in kernelVariant.fields()) {
            val value1 = kernelState[field]
            val value2 = kernelState2[field]
            if (value1 is Array<*> && value2 is Array<*>) {
                assertContentEquals(value1 as Array<Any?>, value2 as Array<Any?>, "Invalid value in field ${field.name}")
            } else {
                assertEquals(kernelState[field], kernelState2[field], "Invalid value in field ${field.name}")
            }
        }
        for (input in kernelVariant.inputs()) {
            when (input) {
                is InputScalar -> assertEquals(kernelState[input], kernelState2[input], "Invalid value in input ${input.name}")
                is InputVector -> assertEquals(kernelState[input], kernelState2[input], "Invalid value in input ${input.name}")
                is InputVectorElement -> assertEquals(kernelState[input], kernelState2[input], "Invalid value in input ${input.name}")
            }
        }
        for (output in kernelVariant.inputs()) {
            when (output) {
                is InputScalar -> assertEquals(kernelState[output], kernelState2[output], "Invalid value in output ${output.name}")
                is InputVector -> assertEquals(kernelState[output], kernelState2[output], "Invalid value in output ${output.name}")
                is InputVectorElement -> assertEquals(kernelState[output], kernelState2[output], "Invalid value in output ${output.name}")
            }
        }
    }
}
