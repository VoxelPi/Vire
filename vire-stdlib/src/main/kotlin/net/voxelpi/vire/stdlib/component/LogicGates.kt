package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.BooleanState
import net.voxelpi.vire.api.simulation.booleanStates
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.stdlib.VireStandardLibrary

val BufferGate = StateMachine.create(Identifier(VireStandardLibrary.id, "buffer")) {

    val input = declareInput("input")
    val output = declareOutput("output")

    update = { context ->
        context[output] = context[input].booleanState()
    }
}

val NotGate = StateMachine.create(Identifier(VireStandardLibrary.id, "not")) {

    val input = declareInput("input")
    val output = declareOutput("output")

    update = { context ->
        context[output] = !context[input].booleanState()
    }
}

val AndGate = StateMachine.create(Identifier(VireStandardLibrary.id, "and")) {

    val inputSize = declareParameter("input_size", 2, min = 2)
    val inputs = declareInput("inputs", inputSize)
    val output = declareOutput("output")

    update = { context ->
        context[output] = BooleanState.and(context.vector(inputs).booleanStates())
    }
}

val OrGate = StateMachine.create(Identifier(VireStandardLibrary.id, "or")) {

    val inputSize = declareParameter("input_size", 2, min = 2)
    val inputs = declareInput("inputs", inputSize)
    val output = declareOutput("output")

    update = { context ->
        context[output] = BooleanState.or(context.vector(inputs).booleanStates())
    }
}

val XorGate = StateMachine.create(Identifier(VireStandardLibrary.id, "xor")) {

    val inputSize = declareParameter("input_size", 2, min = 2)
    val inputs = declareInput("inputs", inputSize)
    val output = declareOutput("output")

    update = { context ->
        context[output] = BooleanState.xor(context.vector(inputs).booleanStates())
    }
}

val NandGate = StateMachine.create(Identifier(VireStandardLibrary.id, "nand")) {

    val inputSize = declareParameter("input_size", 2, min = 2)
    val inputs = declareInput("inputs", inputSize)
    val output = declareOutput("output")

    update = { context ->
        context[output] = BooleanState.nand(context.vector(inputs).booleanStates())
    }
}

val NorGate = StateMachine.create(Identifier(VireStandardLibrary.id, "nor")) {

    val inputSize = declareParameter("input_size", 2, min = 2)
    val inputs = declareInput("inputs", inputSize)
    val output = declareOutput("output")

    update = { context ->
        context[output] = BooleanState.nor(context.vector(inputs).booleanStates())
    }
}

val XnorGate = StateMachine.create(Identifier(VireStandardLibrary.id, "xnor")) {

    val inputSize = declareParameter("input_size", 2, min = 2)
    val inputs = declareInput("inputs", inputSize)
    val output = declareOutput("output")

    update = { context ->
        context[output] = BooleanState.xnor(context.vector(inputs).booleanStates())
    }
}
