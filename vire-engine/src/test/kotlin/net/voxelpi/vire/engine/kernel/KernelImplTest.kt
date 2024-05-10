package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.parameter
import net.voxelpi.vire.engine.kernel.variable.range
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

class KernelImplTest {

    @Test
    fun `create variant`() {
        val parameter1 = parameter("parameter_1", { "mode_a" }) {
            selection("mode_a", "mode_b", "mode_c")
        }
        val parameter2 = parameter("parameter_2", { 3.0 }) {
            range(2.0..3.0)
        }

        val kernel = kernel(Identifier("test", "test")) {
            declare(parameter1)
            declare(parameter2)

            configure = { context ->
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
}
