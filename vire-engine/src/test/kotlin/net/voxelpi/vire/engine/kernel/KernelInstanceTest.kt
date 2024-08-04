package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.createField
import net.voxelpi.vire.engine.kernel.variable.createSetting
import net.voxelpi.vire.engine.kernel.variable.patch.SettingStatePatch
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

        kernelVariant.createInstance(SettingStatePatch(kernelVariant, mapOf("setting_2" to 4))).getOrThrow()
        kernelVariant.createInstance {
            this[setting2] = 5
        }.getOrThrow()
        assertThrows<Exception> { kernelVariant.createInstance().getOrThrow() }
    }

    @Test
    fun `test fields without default initialization`() {
        val setting: Setting<Boolean> = createSetting("initialize")

        val field1: Field<Int> = createField("field_1") {
            initialization = { 10 }
        }
        val field2: Field<Int> = createField("field_2")

        val kernel = kernel {
            declare(setting)
            declare(field1)
            declare(field2)

            onInitialization { context ->
                println(context[field1])
                assertThrows<Exception> { println(context[field2]) }
                if (context[setting]) {
                    context[field2] = 5
                }
            }
        }
        val kernelVariant = kernel.createVariant().getOrThrow()

        kernelVariant.createInstance {
            this[setting] = true
        }.getOrThrow()
        assertThrows<Exception> {
            kernelVariant.createInstance {
                this[setting] = false
            }.getOrThrow()
        }
    }
}
