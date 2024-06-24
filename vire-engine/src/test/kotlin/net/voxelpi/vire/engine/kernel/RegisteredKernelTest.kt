package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.environment.library.KotlinLibrary
import net.voxelpi.vire.engine.kernel.registered.RegisteredKernel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RegisteredKernelTest {

    @Test
    fun `test registered kernels`() {
        val customKernel = kernel {}
        val library = object : KotlinLibrary("test") {
            val customKernel = register("custom", customKernel)
        }

        assertEquals(Identifier("test", "custom"), library.customKernel.id)
        assertEquals(library, library.customKernel.library)

        val variant = library.customKernel.createVariant().getOrThrow()
        assertIs<RegisteredKernel>(variant.kernel)
        assertEquals(Identifier("test", "custom"), (variant.kernel as RegisteredKernel).id)
    }
}
