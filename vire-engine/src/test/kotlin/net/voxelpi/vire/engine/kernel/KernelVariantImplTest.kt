package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.parameter
import net.voxelpi.vire.engine.kernel.variable.setting
import org.junit.jupiter.api.Test
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
}