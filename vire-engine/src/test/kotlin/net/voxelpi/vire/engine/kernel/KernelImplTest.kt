package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.createField
import net.voxelpi.vire.engine.kernel.variable.createInput
import net.voxelpi.vire.engine.kernel.variable.createOutputVector
import net.voxelpi.vire.engine.kernel.variable.createParameter
import net.voxelpi.vire.engine.kernel.variable.createSetting
import net.voxelpi.vire.engine.kernel.variable.inRange
import net.voxelpi.vire.engine.kernel.variable.inSelection
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KernelImplTest {

    @Test
    fun `create variant`() {
        val parameter1 = createParameter("parameter_1") {
            initialization = { "mode_a" }
            constraint = inSelection("mode_a", "mode_b", "mode_c")
        }
        val parameter2 = createParameter("parameter_2") {
            initialization = { 3.0 }
            constraint = inRange(2.0..3.0)
        }

        val kernel = kernel {
            declare(parameter1)
            declare(parameter2)

            onConfiguration { context ->
                if (context[parameter1] == "mode_b" && context[parameter2] > 2.5) {
                    context.signalInvalidConfiguration()
                }
            }
        }

        assertTrue {
            kernel.createVariant().isSuccess
        }

        assertTrue {
            kernel.createVariant {
                this[parameter1] = "mode_b"
                assertThrows<IllegalArgumentException> { this[parameter2] = 1.0 }
                this[parameter2] = 2.75
            }.isFailure
        }

        assertTrue {
            kernel.createVariant {
                this[parameter1] = "mode_b"
                assertThrows<IllegalArgumentException> { this[parameter2] = 1.0 }
                this[parameter2] = 2.25
            }.isSuccess
        }

        assertThrows<IllegalArgumentException> { kernel.createVariant(mapOf<String, Any?>("parameter_1" to "mode_null")) }

        assertTrue {
            kernel.createVariant(mapOf<String, Any?>("parameter_1" to "mode_c", "parameter_2" to 2.75)).isSuccess
        }
    }

    @Test
    fun `test variable accessors`() {
        val parameter1 = createParameter("parameter_1") {
            initialization = { "mode_a" }
            constraint = inSelection("mode_a", "mode_b", "mode_c")
        }
        val parameter2 = createParameter("parameter_2") {
            initialization = { 3.0 }
            constraint = inRange(2.0..3.0)
        }
        val setting1 = createSetting<Double>("setting_1") {
            initialization = { 0.0 }
        }
        val field1 = createField<Double>("field_1") {
            initialization = { 0.0 }
        }
        val input1 = createInput("input_1")
        val output1 = createOutputVector("output_1") {
            size = { 10 }
        }

        val kernel = kernel {
            declare(parameter1)
            declare(setting1)
            declare(field1)
            declare(input1)
            declare(output1)

            onConfiguration { context ->
                if (context[parameter1] == "mode_b" && context[parameter2] > 2.5) {
                    context.signalInvalidConfiguration()
                }
            }
        }

        assertTrue { kernel.hasVariable(parameter1.name) }
        assertFalse { kernel.hasVariable(parameter2.name) }
        assertTrue { kernel.hasVariable(setting1.name) }
        assertTrue { kernel.hasVariable(field1.name) }
        assertTrue { kernel.hasVariable(input1.name) }
        assertTrue { kernel.hasVariable(output1.name) }
    }
}
