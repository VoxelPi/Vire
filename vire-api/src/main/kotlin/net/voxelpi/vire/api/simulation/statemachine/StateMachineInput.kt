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
}

/**
 * An input vector of a state machine.
 */
interface StateMachineInputVector : StateMachineIOStateVector {

    /**
     * The name of the input vector.
     */
    override val name: String
}

/**
 * Creates a new input.
 */
fun input(name: String): StateMachineInput {
    return Vire.stateMachineFactory.get().createInput(name)
}

/**
 * Creates a new input vector.
 */
fun inputVector(name: String, initialSize: Int): StateMachineInputVector {
    return Vire.stateMachineFactory.get().createInputVector(name, StateMachineStateVector.InitialSizeProvider.Value(initialSize))
}

/**
 * Creates a new input vector.
 */
fun inputVector(name: String, initialSize: StateMachineParameter<Number>): StateMachineInputVector {
    return Vire.stateMachineFactory.get().createInputVector(name, StateMachineStateVector.InitialSizeProvider.Parameter(initialSize))
}
