package net.voxelpi.vire.stdlib.component

import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.api.simulation.network.NetworkState
import net.voxelpi.vire.stdlib.VireStandardLibrary

object Output : StateMachine(VireStandardLibrary, "output") {

    val value = declarePublic("value", true)
    val channels = declarePublic("channels", 1)
    val output = declareOutput("output")

    override fun tick(context: StateMachineContext) {
        context[output] = NetworkState.value(context[value], context[channels])
    }
}
