package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.max
import net.voxelpi.vire.engine.kernel.variable.min
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.parameter
import net.voxelpi.vire.engine.kernel.variable.range
import net.voxelpi.vire.engine.kernel.variable.setting
import org.junit.jupiter.api.Test

class KernelImplTest {

    @Test
    fun test1() {
        val input = input("A")

        val parameterMode = parameter("mode", { "mode_a" }) {
            selection("mode_a", "mode_b", "mode_c")
        }

        val parameter1 = parameter("test", { 2 }) {
            predicate { it % 2 == 0 }
            range(2..3)
            any {
                min(1)
                all {
                    max(4)
                }
            }
        }
        val parameter2 = parameter("test2", { 3.0 }) {
            range(2.0..3.0)
        }
        val parameter3 = parameter("test3", { 3F }) {
            range(2F..3F)
        }

        val setting = setting("mode", { "mode_a" }) {
            selection("mode_a", "mode_b", "mode_c")
        }

        val kernel = kernel(Identifier("test", "test")) {
            declare(parameterMode)
            declare(parameter1)
            declare(parameter2)
            declare(parameter3)
            declare(input)
            val output = declare(output("B", 2))

            configure = { context ->
                if (context[parameterMode] == "mode_a") {
                    context.declare(setting)
                }
            }
        }
    }
}
