package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.VariableInitialization
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.max
import net.voxelpi.vire.engine.kernel.variable.min
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.parameter
import net.voxelpi.vire.engine.kernel.variable.range
import org.junit.jupiter.api.Test

class KernelImplTest {

    @Test
    fun test1() {
        val input = input("A")

        val parameter1 = parameter("test", VariableInitialization.constant(3)) {
            predicate { it % 2 == 0 }
            range(2..3)
            any {
                min(1)
                all {
                    max(4)
                }
            }
        }
        val parameter2 = parameter("test2", VariableInitialization.constant(3.0)) {
            range(2.0..3.0)
        }
        val parameter3 = parameter("test3", VariableInitialization.constant(3F)) {
            range(2F..3F)
        }

        val kernel = kernel(Identifier("test", "test")) {
            declare(parameter1)
            declare(parameter2)
            declare(parameter3)
            declare(input)
            val output = declare(output("B", 2))

            configure = { _ ->
//                context.declare()
//                declare()
            }
        }
    }
}
