package net.voxelpi.vire.api.simulation.statemachine

import net.voxelpi.vire.api.Vire

/**
 * An output of a state machine.
 */
interface StateMachineOutput : StateMachineIOState {

    /**
     * the name of the output.
     */
    override val name: String

    /**
     * The initial size of the output.
     */
    override val initialSize: StateMachineIOState.InitialSizeProvider
}

/**
 * Creates a new output.
 */
fun output(
    name: String,
    initialSize: Int = 1,
): StateMachineOutput {
    return Vire.stateMachineFactory.get().createOutput(name, StateMachineIOState.InitialSizeProvider.Value(initialSize))
}

/**
 * Creates a new output.
 */
fun output(
    name: String,
    initialSize: StateMachineParameter<Number>,
): StateMachineOutput {
    return Vire.stateMachineFactory.get().createOutput(name, StateMachineIOState.InitialSizeProvider.Parameter(initialSize))
}
