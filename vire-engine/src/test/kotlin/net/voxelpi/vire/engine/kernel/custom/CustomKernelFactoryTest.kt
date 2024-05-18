package net.voxelpi.vire.engine.kernel.custom

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.custom.constraint.IntMin
import net.voxelpi.vire.engine.kernel.custom.declaration.FieldDeclaration
import net.voxelpi.vire.engine.kernel.custom.declaration.InputDeclaration
import net.voxelpi.vire.engine.kernel.custom.declaration.OutputDeclaration
import net.voxelpi.vire.engine.kernel.custom.declaration.ParameterDeclaration
import net.voxelpi.vire.engine.kernel.custom.declaration.SettingDeclaration
import net.voxelpi.vire.engine.kernel.custom.size.ParametricSize
import org.junit.jupiter.api.Test

class CustomKernelFactoryTest {

    @Test
    fun `test scalar buffer kernel generation`() {
        val kernel = generateKernel<ScalarBuffer>()
    }

    @KernelDefinition("vire", "buffer/scalar")
    class ScalarBuffer : CustomKernel() {

        @InputDeclaration
        lateinit var input: LogicState

        @OutputDeclaration
        lateinit var output: LogicState

        override fun update() {
            input = output
        }
    }

    @KernelDefinition("vire", "buffer/vector")
    class VectorBuffer : CustomKernel() {

        @ParameterDeclaration
        @IntMin(1)
        var size: Int = 1

        @InputDeclaration
        @ParametricSize("size")
        lateinit var inputs: Array<LogicState>

        @OutputDeclaration
        @ParametricSize("size")
        lateinit var outputs: Array<LogicState>

        override fun update() {
            inputs = outputs
        }
    }

    @KernelDefinition("vire", "counter")
    class Counter : CustomKernel() {

        @SettingDeclaration("counter_step")
        @IntMin(3)
        var counterStep: Int = 1

        @FieldDeclaration
        var counter: Int = 0

        override fun update() {
            counter += counterStep
        }
    }
}
