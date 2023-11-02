package net.voxelpi.vire.api.simulation.component

/**
 * An output of a state machine.
 *
 * @property name the name of the input.
 * @property initialSize the initial number of variables in the output vector.
 */
data class StateMachineOutput(
    override val name: String,
    val initialSize: Int = 1,
) : ComponentPortVariable
