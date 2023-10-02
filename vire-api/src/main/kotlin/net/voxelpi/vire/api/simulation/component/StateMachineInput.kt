package net.voxelpi.vire.api.simulation.component

/**
 * An input of a state machine.
 *
 * @property name the name of the input.
 * @property initialSize the initial number of variables in the input vector.
 */
class StateMachineInput(
    override val name: String,
    val initialSize: Int = 1,
) : ComponentPortVariable
