package net.voxelpi.vire.serialization

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.Vire
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.stdlib.VireStandardLibrary
import net.voxelpi.vire.stdlib.kernel.BufferGate
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
        val component1 = circuit.createComponent(BufferGate.kernel.createVariant().getOrThrow())
        val port11 = component1.createPort(BufferGate.input)
        val port12 = component1.createPort(BufferGate.output)

        val component2 = circuit.createComponent(NotGate.kernel.createVariant().getOrThrow())
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
}
