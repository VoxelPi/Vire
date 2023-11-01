package net.voxelpi.vire.stdlib.component.logic

import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.api.simulation.component.StateMachineParameter
import net.voxelpi.vire.api.simulation.network.NetworkState
import net.voxelpi.vire.stdlib.VireStandardLibrary

object NorGate : StateMachine(VireStandardLibrary, "nor") {

    val inputCount = declare(StateMachineParameter.Int("input_count", 2, min = 2))
    val input = declareInput("input", 2)
    val output = declareOutput("output")

    override fun init(context: StateMachineContext) {
        context.resize(input, context[inputCount])
    }

    override fun tick(context: StateMachineContext) {
        context[output] = NetworkState.nor(context.vector(input))
    }
}
