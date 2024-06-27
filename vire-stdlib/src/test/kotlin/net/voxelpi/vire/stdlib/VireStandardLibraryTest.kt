package net.voxelpi.vire.stdlib

import net.voxelpi.vire.engine.Vire
import org.junit.jupiter.api.Test

class VireStandardLibraryTest {

    @Test
    fun `load stdlib`() {
        Vire.createEnvironment(listOf(VireStandardLibrary))
    }
}
