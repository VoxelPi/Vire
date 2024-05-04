package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import org.junit.jupiter.api.Test

class KernelImplTest {

    @Test
    fun test1() {
        val kernel = kernel(Identifier("test", "test")) {
            configure = { context ->
//                context.declare()
//                declare()
            }
        }
    }
}
