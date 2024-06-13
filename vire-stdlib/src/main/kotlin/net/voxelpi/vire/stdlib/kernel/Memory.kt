package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.BooleanState
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.field
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.min
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.parameter
import net.voxelpi.vire.engine.kernel.variable.range
import net.voxelpi.vire.engine.kernel.variable.setting

public object Memory : KernelProvider {

    public val readOnly: Parameter<Boolean> = parameter("read_only", initialization = { true })

    public val addressBits: Setting<Int> = setting("address_bits", initialization = { 8 }) {
        range(1..31)
    }
    public val wordSize: Setting<Int> = setting("word_size", initialization = { 8 }) {
        min(1)
    }

    public val readActive: InputScalar = input("read_active")
    public val readAddress: InputScalar = input("read_address")
    public val readValue: OutputScalar = output("read_value")
    public val writeActive: InputScalar = input("write_active")
    public val writeAddress: InputScalar = input("write_address")
    public val writeValue: InputScalar = input("write_value")

    public val memory: Field<Array<BooleanState>> = field("memory", initialization = { emptyArray() })

    override val kernel: Kernel = kernel {
        declare(memory)
        declare(addressBits)
        declare(wordSize)
        declare(readOnly)
        declare(readActive)
        declare(readAddress)
        declare(readValue)

        onConfiguration { context ->
            // Declare write io state variables if read only is set to false.
            if (!context[readOnly]) {
                context.declare(writeActive)
                context.declare(writeAddress)
                context.declare(writeValue)
            }
        }

        onInitialization { context ->
            // Create the internal memory.
            val size = 1 shl context[addressBits]
            val entrySize = context[wordSize]
            context[memory] = Array(size) { BooleanState(entrySize) { false } }
        }

        onUpdate { context ->
            // Output the read value if the readActive bit is set. Otherwise, leave the output value unassigned.
            if (context[readActive].toBoolean()) {
                context[readValue] = context[memory][context[readAddress].booleanState().toInt()]
            } else {
                context[readValue] = LogicState.EMPTY
            }

            // Update the value in the memory at the write address if read only bit is clear and writeActive bit is set.
            if (!context[readOnly] && context[writeActive].toBoolean()) {
                context[memory][context[writeAddress].booleanState().toInt()] = context[writeValue].booleanState(context[wordSize])
            }
        }
    }
}
