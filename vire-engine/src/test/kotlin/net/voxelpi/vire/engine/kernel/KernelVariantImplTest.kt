package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.createField
import net.voxelpi.vire.engine.kernel.variable.createParameter
import net.voxelpi.vire.engine.kernel.variable.createSetting
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KernelVariantImplTest {

    @Test
    fun `test variant variables`() {
        val parameter1 = createParameter("parameter_1", { "mode_a" }) {
            selection("mode_a", "mode_b", "mode_c")
        }
        val setting1 = createSetting("setting_1", { 0.0 })

        val kernel = kernel {
            declare(parameter1)

            onConfiguration { context ->
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
        val setting1 = createSetting("setting_1", { 10.0 })
        val kernel = kernel {
            declare(setting1)
        }

        val variant = kernel.createVariant().getOrThrow()
        val instance = variant.createInstance().getOrThrow()
        assertEquals(10.0, instance[setting1])
    }

    @Test
    fun `throw on variable declaration after a kernel has been build`() {
        val field1 = createField("field_1", initialization = { "test" })

        val kernel = kernel {
            onConfiguration { _ ->
                // Check if an exception is being thrown when accidentally registering the variable using the wrong declare method.
                assertThrows<Exception> { declare(field1) }
            }
        }

        kernel.createVariant().getOrThrow()
    }
}
