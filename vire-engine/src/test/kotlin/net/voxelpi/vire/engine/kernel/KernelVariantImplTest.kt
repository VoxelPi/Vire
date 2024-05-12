package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.field
import net.voxelpi.vire.engine.kernel.variable.parameter
import net.voxelpi.vire.engine.kernel.variable.setting
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KernelVariantImplTest {

    @Test
    fun `test variant variables`() {
        val parameter1 = parameter("parameter_1", { "mode_a" }) {
            selection("mode_a", "mode_b", "mode_c")
        }
        val setting1 = setting("setting_1", { 0.0 })

        val kernel = kernel(Identifier("test", "test")) {
            declare(parameter1)

            configure = { context ->
                if (context[parameter1] == "mode_b") {
                    context.declare(setting1)
                }
            }
        }
        val variant1 = kernel.createVariant {
            this[parameter1] = "mode_a"
        }.getOrThrow()
        val variant2 = kernel.createVariant {
            this[parameter1] = "mode_b"
        }.getOrThrow()

        assertFalse { kernel.hasVariable(setting1.name) }
        assertFalse { variant1.hasVariable(setting1.name) }
        assertTrue { variant2.hasVariable(setting1.name) }
    }

    @Test
    fun `create instance`() {
        val setting1 = setting("setting_1", { 10.0 })
        val kernel = kernel(Identifier("test", "test")) {
            declare(setting1)
        }

        val variant = kernel.createVariant().getOrThrow()
        val instance = variant.createInstance().getOrThrow()
        assertEquals(10.0, instance[setting1])
    }

    @Test
    fun `throw on variable declaration after a kernel has been build`() {
        val field1 = field("field_1", initialization = { "test" })

        val kernel = kernel(Identifier("test", "test")) {
            configure = { _ ->
                // Check if an exception is being thrown when accidentally registering the variable using the wrong declare method.
                assertThrows<Exception> { declare(field1) }
            }
        }

        kernel.createVariant().getOrThrow()
    }
}
