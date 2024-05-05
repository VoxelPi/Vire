package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.VariableInitialization
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.parameter
import org.junit.jupiter.api.Test

class KernelImplTest {

    @Test
    fun test1() {
        val input = input("A")

        val parameter = parameter("test", VariableInitialization.constant(3)) { it % 2 == 0 }

        val kernel = kernel(Identifier("test", "test")) {
            declare(input)
            val output = declare(output("B", 2))

            configure = { context ->
//                context.declare()
//                declare()
            }
        }
    }
}
