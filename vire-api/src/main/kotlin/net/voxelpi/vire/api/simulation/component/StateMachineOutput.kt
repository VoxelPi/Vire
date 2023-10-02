package net.voxelpi.vire.api.simulation.component

import net.voxelpi.vire.api.simulation.network.NetworkState

/**
 * An output of a state machine.
 *
 * @property name the name of the input.
 * @property initialSize the initial number of variables in the output vector.
 * @property initialValue the initial value of a variable in the output vector.
 */
data class StateMachineOutput(
    override val name: String,
    val initialSize: Int = 1,
    val initialValue: NetworkState = NetworkState.None,
) : ComponentPortVariable
