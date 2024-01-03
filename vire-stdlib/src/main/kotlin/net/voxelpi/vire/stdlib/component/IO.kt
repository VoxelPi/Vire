package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.LogicValue
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.stdlib.VireStandardLibrary

val Input = StateMachine.create(Identifier(VireStandardLibrary.id, "input")) {

    val value = declareParameter("value", LogicValue.NONE)
    val channels = declareParameter("channels", 1, min = 1)
    val output = declareOutput("output")

    configure = { context ->
        context[output] = LogicState.value(context[value], context[channels])
    }
}

val Output = StateMachine.create(Identifier(VireStandardLibrary.id, "output")) {

    @Suppress("UNUSED_VARIABLE")
    val input = declareInput("input")
}
