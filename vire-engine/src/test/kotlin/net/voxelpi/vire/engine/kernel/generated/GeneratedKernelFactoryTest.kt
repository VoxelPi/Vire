package net.voxelpi.vire.engine.kernel.generated

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.Vire
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.kernel.generated.constraint.IntMin
import net.voxelpi.vire.engine.kernel.generated.declaration.FieldDeclaration
import net.voxelpi.vire.engine.kernel.generated.declaration.InputDeclaration
import net.voxelpi.vire.engine.kernel.generated.declaration.OutputDeclaration
import net.voxelpi.vire.engine.kernel.generated.declaration.ParameterDeclaration
import net.voxelpi.vire.engine.kernel.generated.declaration.SettingDeclaration
import net.voxelpi.vire.engine.kernel.generated.size.ParametricSize
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.variableOfKind
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GeneratedKernelFactoryTest {

    private lateinit var environment: Environment

    @BeforeEach
    fun setUp() {
        environment = Vire.createEnvironmentImpl(emptyList())
    }

    @KernelDefinition
    @Tagged("vire:test1", "vire:test2")
    @Tagged("test:test3", "vire:test1")
    @WithProperty("vire:p1", "Hello")
    @WithProperty("vire:p2", "World!")
    @WithProperty("vire:p1", "Hello, World!")
    class UnitKernel : GeneratedKernel()

    @Test
    fun `test unit kernel generation`() {
        val kernel = generateKernel<UnitKernel>()

        // Verify tags.
        assertEquals(
            setOf(
                Identifier("vire", "test1"),
                Identifier("vire", "test2"),
                Identifier("test", "test3"),
            ),
            kernel.tags
        )

        // Verify properties
        assertEquals(
            mapOf(
                Identifier("vire", "p1") to "Hello, World!",
                Identifier("vire", "p2") to "World!",
            ),
            kernel.properties
        )
    }

    @Test
    fun `test scalar buffer kernel generation`() {
        val kernelVariant = generateKernel<ScalarBuffer>().createVariant().getOrThrow()
        val simulation = environment.createSimulation(kernelVariant.createInstance().getOrThrow())

        val input = kernelVariant.variableOfKind<InputScalar>("input")!!
        val output = kernelVariant.variableOfKind<OutputScalar>("output")!!
        val state = LogicState.value(true, 2)

        simulation.modifyInputs {
            this[input] = state
        }
        simulation.simulateStep()
        assertEquals(state, simulation.state[output])
    }

    @KernelDefinition
    class ScalarBuffer : GeneratedKernel() {

        @InputDeclaration
        lateinit var input: LogicState

        @OutputDeclaration
        lateinit var output: LogicState

        override fun update() {
            output = input
        }
    }

    @Test
    fun `test vector buffer kernel generation`() {
        val kernelVariant = generateKernel<VectorBuffer>().createVariant(mapOf("size" to 2)).getOrThrow()
        assertEquals(2, kernelVariant.size("inputs"))
        assertEquals(2, kernelVariant.size("outputs"))

        val simulation = environment.createSimulation(kernelVariant.createInstance().getOrThrow())

        val inputs = kernelVariant.variableOfKind<InputVector>("inputs")!!
        val outputs = kernelVariant.variableOfKind<OutputVector>("outputs")!!
        val state1 = LogicState.value(true, 2)
        val state2 = LogicState.value(false, 3)

        simulation.modifyInputs {
            this[inputs[0]] = state1
            this[inputs[1]] = state2
        }
        simulation.simulateStep()
        assertEquals(state1, simulation.state[outputs[0]])
        assertEquals(state2, simulation.state[outputs[1]])
    }

    @KernelDefinition
    class VectorBuffer : GeneratedKernel() {

        @ParameterDeclaration
        @IntMin(1)
        var size: Int = 1

        @InputDeclaration
        @ParametricSize("size")
        lateinit var inputs: Array<LogicState>

        @OutputDeclaration
        @ParametricSize("size")
        lateinit var outputs: Array<LogicState>

        override fun update() {
            outputs = inputs
        }
    }

    @Test
    fun `test counter kernel generation`() {
        val kernelVariant = generateKernel<Counter>().createVariant().getOrThrow()
        val kernelInstance = kernelVariant.createInstance(mapOf("counter_step" to 4)).getOrThrow()
        val simulation = environment.createSimulation(kernelInstance)

        val counter = kernelVariant.variableOfKind<Field<Int>>("counter")!!
        assertEquals(1, simulation.state[counter])
        simulation.simulateStep()
        assertEquals(5, simulation.state[counter])
        simulation.simulateStep()
        assertEquals(9, simulation.state[counter])
        simulation.simulateSteps(10)
        assertEquals(49, simulation.state[counter])
    }

    @KernelDefinition
    class Counter : GeneratedKernel() {

        @SettingDeclaration("counter_step")
        @IntMin(1)
        var counterStep: Int = 1

        @FieldDeclaration
        var counter: Int = 1

        override fun update() {
            counter += counterStep
        }
    }
}
