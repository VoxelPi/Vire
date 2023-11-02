package net.voxelpi.vire.api.simulation.component

/**
 * An input vector of a state machine.
 * Consists of multiple elements that each can be bound to different component ports (or no port at all).
 *
 * @property name the name of the input.
 * @property initialSize the initial number of variables in the input vector.
 */
class StateMachineInput(
    override val name: String,
    val initialSize: Int = 1,
) : ComponentPortVector
