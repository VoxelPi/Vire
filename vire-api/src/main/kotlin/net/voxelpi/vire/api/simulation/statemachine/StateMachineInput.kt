package net.voxelpi.vire.api.simulation.statemachine

import net.voxelpi.vire.api.Vire

/**
 * An input of a state machine.
 */
interface StateMachineInput : StateMachineIOState {

    /**
     * The name of the input.
     */
    override val name: String

    /**
     * The initial size of the input.
     */
    override val initialSize: StateMachineIOState.InitialSizeProvider
}

/**
 * Creates a new input.
 */
fun input(
    name: String,
    initialSize: Int = 1,
): StateMachineInput {
    return Vire.stateMachineFactory.get().createInput(name, StateMachineIOState.InitialSizeProvider.Value(initialSize))
}

/**
 * Creates a new input.
 */
fun input(
    name: String,
    initialSize: StateMachineParameter<out Number>,
): StateMachineInput {
    return Vire.stateMachineFactory.get().createInput(name, StateMachineIOState.InitialSizeProvider.Parameter(initialSize))
}
