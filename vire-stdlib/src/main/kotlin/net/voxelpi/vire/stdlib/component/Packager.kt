package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.stdlib.VireStandardLibrary

val Packager = StateMachine.create(Identifier(VireStandardLibrary.id, "packager")) {

    val inputCount = declareParameter("input_count", 2, min = 2)
    val input = declareInput("input", inputCount)
    val output = declareOutput("output")

    update = { context ->
        context[output] = LogicState(context[inputCount]) { index ->
            context[input, index][0]
        }
    }
}

val Unpackager = StateMachine.create(Identifier(VireStandardLibrary.id, "unpackager")) {

    val outputCount = declareParameter("output_count", 2, min = 2)
    val input = declareInput("input")
    val output = declareOutput("output", outputCount)

    update = { context ->
        for (index in 0..<context[outputCount]) {
            context[output, index] = LogicState.value(context[input].channelOrNone(0))
        }
    }
}
