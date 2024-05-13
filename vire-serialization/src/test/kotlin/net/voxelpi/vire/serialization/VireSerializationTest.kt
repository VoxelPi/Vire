package net.voxelpi.vire.serialization

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.Vire
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.stdlib.VireStandardLibrary
import net.voxelpi.vire.stdlib.kernel.BufferGate
import net.voxelpi.vire.stdlib.kernel.Memory
import net.voxelpi.vire.stdlib.kernel.NotGate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VireSerializationTest {

    private lateinit var environment: Environment
    private lateinit var circuit: Circuit

    @BeforeEach
    fun setUp() {
        environment = Vire.createEnvironment(listOf(VireStandardLibrary))
        circuit = environment.createCircuit(Identifier("vire-test", "test"))
    }

    @Test
    fun `simple test`() {
        val component1 = circuit.createComponent(BufferGate.createVariant().getOrThrow())
        val port11 = component1.createPort(BufferGate.input)
        val port12 = component1.createPort(BufferGate.output)

        val component2 = circuit.createComponent(NotGate.createVariant().getOrThrow())
        val port21 = component2.createPort(NotGate.input)
        val port22 = component2.createPort(NotGate.output)

        circuit.createNetworkConnection(port12.networkNode, port21.networkNode)
        circuit.createNetworkConnection(port11.networkNode, port22.networkNode)

        // Serialize the circuit
        val serializedCircuit = VireSerialization.serialize(environment, circuit, "  ")

        // Deserialize the circuit
        val circuit2 = VireSerialization.deserialize(environment, serializedCircuit).getOrThrow()

        assertEquals(circuit.id, circuit2.id)
        assertEquals(circuit.components().size, circuit2.components().size)
        assertEquals(circuit.networks().size, circuit2.networks().size)
    }

    @Test
    fun `complex component`() {
        val kernelVariant = Memory.createVariant {
            this[Memory.readOnly] = true
            this[Memory.wordSize] = 16
            this[Memory.addressBits] = 4
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
        val serializedCircuit = VireSerialization.serialize(environment, circuit, "  ")
//        print(serializedCircuit)

        // Deserialize the circuit
        val circuit2 = VireSerialization.deserialize(environment, serializedCircuit).getOrThrow()

        assertEquals(circuit.id, circuit2.id)
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
    }
}
