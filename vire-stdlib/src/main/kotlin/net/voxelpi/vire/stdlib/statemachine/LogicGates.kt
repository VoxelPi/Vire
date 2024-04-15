package net.voxelpi.vire.stdlib.statemachine

import net.voxelpi.vire.api.BooleanState
import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.booleanStates
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.StateMachineProvider
import net.voxelpi.vire.api.circuit.statemachine.input
import net.voxelpi.vire.api.circuit.statemachine.output
import net.voxelpi.vire.api.circuit.statemachine.parameter
import net.voxelpi.vire.stdlib.VIRE_STDLIB_ID

object BufferGate : StateMachineProvider {
    val input = input("input")
    val output = output("output")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "buffer")) {
        declare(input)
        declare(output)

        update = { context ->
            context[output] = context[input].booleanState()
        }
    }
}

object NotGate : StateMachineProvider {
    val input = input("input")
    val output = output("output")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "not")) {
        declare(BufferGate.input)
        declare(BufferGate.output)

        update = { context ->
            context[output] = !context[input].booleanState()
        }
    }
}

object AndGate : StateMachineProvider {
    val inputSize = parameter("input_size", 2, min = 2)
    val inputs = input("inputs", inputSize)
    val output = output("output")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "and")) {
        declare(inputSize)
        declare(inputs)
        declare(output)

        update = { context ->
            context[output] = BooleanState.and(context.vector(inputs).booleanStates())
        }
    }
}

object OrGate : StateMachineProvider {
    val inputSize = parameter("input_size", 2, min = 2)
    val inputs = input("inputs", inputSize)
    val output = output("output")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "or")) {
        declare(inputSize)
        declare(inputs)
        declare(output)

        update = { context ->
            context[output] = BooleanState.or(context.vector(inputs).booleanStates())
        }
    }
}

object XorGate : StateMachineProvider {
    val inputSize = parameter("input_size", 2, min = 2)
    val inputs = input("inputs", inputSize)
    val output = output("output")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "xor")) {
        declare(inputSize)
        declare(inputs)
        declare(output)

        update = { context ->
            context[output] = BooleanState.xor(context.vector(inputs).booleanStates())
        }
    }
}

object NandGate : StateMachineProvider {
    val inputSize = parameter("input_size", 2, min = 2)
    val inputs = input("inputs", inputSize)
    val output = output("output")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "nand")) {
        declare(inputSize)
        declare(inputs)
        declare(output)

        update = { context ->
            context[output] = BooleanState.nand(context.vector(inputs).booleanStates())
        }
    }
}

object NorGate : StateMachineProvider {
    val inputSize = parameter("input_size", 2, min = 2)
    val inputs = input("inputs", inputSize)
    val output = output("output")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "nor")) {
        declare(inputSize)
        declare(inputs)
        declare(output)

        update = { context ->
            context[output] = BooleanState.nor(context.vector(inputs).booleanStates())
        }
    }
}

object XnorGate : StateMachineProvider {
    val inputSize = parameter("input_size", 2, min = 2)
    val inputs = input("inputs", inputSize)
    val output = output("output")

    override val stateMachine = StateMachine.create(Identifier(VIRE_STDLIB_ID, "xnor")) {
        declare(inputSize)
        declare(inputs)
        declare(output)

        update = { context ->
            context[output] = BooleanState.xnor(context.vector(inputs).booleanStates())
        }
    }
}
