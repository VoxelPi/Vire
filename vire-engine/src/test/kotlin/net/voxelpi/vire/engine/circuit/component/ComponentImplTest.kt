package net.voxelpi.vire.engine.circuit.component

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.Vire
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.setting
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ComponentImplTest {

    private lateinit var environment: Environment
    private lateinit var circuit: Circuit

    @BeforeEach
    fun setUp() {
        environment = Vire.createEnvironmentImpl(emptyList())
        circuit = environment.createCircuit(Identifier("vire-test", "test"))
    }

    @Test
    fun `create component`() {
        val setting1 = setting("setting_1", initialization = { "Test" })

        val kernel = kernel(Identifier("test", "test")) {
            declare(setting1)
        }
        val kernelVariant = kernel.createVariant().getOrThrow()

        val component = circuit.createComponent(kernelVariant)
        component.configuration[setting1] = ComponentConfiguration.Entry.Value("test")

        val circuitKernel = circuit.createKernel()
        val circuitKernelInstance = circuitKernel.createVariant().getOrThrow().createInstance()
    }
}
