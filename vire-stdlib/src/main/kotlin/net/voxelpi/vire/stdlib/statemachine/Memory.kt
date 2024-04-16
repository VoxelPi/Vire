package net.voxelpi.vire.stdlib.statemachine

import net.voxelpi.vire.api.BooleanState
import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.StateMachineProvider
import net.voxelpi.vire.api.circuit.statemachine.input
import net.voxelpi.vire.api.circuit.statemachine.output
import net.voxelpi.vire.api.circuit.statemachine.parameter
import net.voxelpi.vire.api.circuit.statemachine.variable
import net.voxelpi.vire.stdlib.VIRE_STDLIB_ID

object Memory : StateMachineProvider {

    val addressBits = parameter("address_bits", 8, min = 0, max = 31)
    val wordSize = parameter("word_size", 8, min = 1)
    val readOnly = parameter("read_only", true)

    val readActive = input("read_active")
    val readAddress = input("read_address")
    val readValue = output("read_value")
    val writeActive = input("write_active")
    val writeAddress = input("write_address")
    val writeValue = input("write_value")

    val memory = variable("memory", Array(256) { BooleanState(8) { false } })

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "memory")) {
        declare(addressBits)
        declare(wordSize)
        declare(readOnly)
        declare(readActive)
        declare(readAddress)
        declare(readValue)
        declare(writeActive)
        declare(writeAddress)
        declare(writeValue)

        configure = { context ->
            // Disable write io state variables if read only is set to true.
            if (context[readOnly]) {
                context.resize(writeActive, 0)
                context.resize(writeAddress, 0)
                context.resize(writeValue, 0)
            }

            // Create the internal memory.
            val size = 1 shl context[addressBits]
            val entrySize = context[wordSize]
            context[memory] = Array(size) { BooleanState(entrySize) { false } }
        }

        update = { context ->
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
