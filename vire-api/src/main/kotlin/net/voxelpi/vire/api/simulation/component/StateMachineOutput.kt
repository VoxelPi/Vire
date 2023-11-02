package net.voxelpi.vire.api.simulation.component

/**
 * An output vector of a state machine.
 * Consists of multiple elements that each can be bound to different component ports (or no port at all).
 *
 * @property name the name of the output.
 * @property initialSize the initial number of variables in the output vector.
 */
data class StateMachineOutput(
    override val name: String,
    val initialSize: Int = 1,
) : ComponentPortVector

/**
 * Creates a new output.
 */
fun output(name: String, initialSize: Int = 1): StateMachineOutput {
    return StateMachineOutput(name, initialSize)
}
