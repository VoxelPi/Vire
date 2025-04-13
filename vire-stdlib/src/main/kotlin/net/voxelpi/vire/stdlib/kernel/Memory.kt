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
import net.voxelpi.vire.engine.kernel.variable.atLeast
import net.voxelpi.vire.engine.kernel.variable.createField
import net.voxelpi.vire.engine.kernel.variable.createInput
import net.voxelpi.vire.engine.kernel.variable.createOutput
import net.voxelpi.vire.engine.kernel.variable.createParameter
import net.voxelpi.vire.engine.kernel.variable.createSetting
import net.voxelpi.vire.engine.kernel.variable.inRange

public object Memory : KernelProvider {

    public val readOnly: Parameter<Boolean> = createParameter("read_only") {
        initialization = { true }
        description = "If the memory should be read only. This disables all variables related to writing to the memory element."
    }

    public val addressBits: Setting<Int> = createSetting("address_bits") {
        initialization = { 8 }
        constraint = inRange(1..31)
        description = "The number of address bits."
    }
    public val wordSize: Setting<Int> = createSetting("word_size") {
        initialization = { 8 }
        constraint = atLeast(1)
        description = "The size of the words stored in the memory."
    }

    public val readActive: InputScalar = createInput("read_active") {
        description = "If the state of the stored word at the current `read_address` should be written to the `read_value` output."
    }
    public val readAddress: InputScalar = createInput("read_address") {
        description = "The address from which should be read."
    }
    public val readValue: OutputScalar = createOutput("read_value") {
        description = "The value that is being read. If `read_active` is false, this is `NONE`"
    }
    public val writeActive: InputScalar = createInput("write_active") {
        description = "If the state of the `write_value` input should be written to the current `write_address`."
    }
    public val writeAddress: InputScalar = createInput("write_address") {
        description = "The address to which should be written."
    }
    public val writeValue: InputScalar = createInput("write_value") {
        description = "The value that should be stored."
    }

//    public val memory: Field<Array<BooleanState>> = createField("memory", initialization = { emptyArray() })

    public val memory: Field<Array<BooleanState>> = createField("memory") {
        initialization = { emptyArray() }
    }

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
