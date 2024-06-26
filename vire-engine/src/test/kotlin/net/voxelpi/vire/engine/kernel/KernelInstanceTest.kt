package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.createSetting
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class KernelInstanceTest {

    @Test
    fun `test optional and required settings`() {
        val setting1: Setting<Int> = createSetting("setting_1") {
            initialization = { 10 }
        }
        val setting2: Setting<Int> = createSetting("setting_2")

        val kernel = kernel {
            declare(setting1)
            declare(setting2)

            onInitialization { context ->
                println("$setting1: ${context[setting1]} ${context[setting2]}")
            }
        }
        val kernelVariant = kernel.createVariant().getOrThrow()

        kernelVariant.createInstance(mapOf("setting_2" to 4)).getOrThrow()
        kernelVariant.createInstance {
            this[setting2] = 5
        }.getOrThrow()
        assertThrows<Exception> { kernelVariant.createInstance().getOrThrow() }
    }
}
