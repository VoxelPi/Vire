package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.api.simulation.network.NetworkState
import net.voxelpi.vire.stdlib.VireStandardLibrary
import net.voxelpi.vire.stdlib.component.logic.AndGate

object Packager : StateMachine(VireStandardLibrary, "packager") {

    val inputCount = declareParameter("input_count", 2, min = 2)
    val input = declareInput("input", 2)
    val output = declareOutput("output")

    override fun configure(context: StateMachineContext) {
        context.resize(AndGate.input, context[AndGate.inputCount])
    }

    override fun tick(context: StateMachineContext) {
        context[output] = NetworkState.Value(
            BooleanArray(context[inputCount]) { index ->
                (context[input, index] as NetworkState.Value).value[0]
            }
        )
    }
}
