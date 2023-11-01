package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.api.simulation.network.NetworkState
import net.voxelpi.vire.stdlib.VireStandardLibrary

object Output : StateMachine(VireStandardLibrary, "output") {

    val value = declareParameter("value", true)
    val channels = declareParameter("channels", 1, min = 1)
    val output = declareOutput("output")

    override fun configure(context: StateMachineContext) {
        context[output] = NetworkState.value(context[value], context[channels])
    }
}
