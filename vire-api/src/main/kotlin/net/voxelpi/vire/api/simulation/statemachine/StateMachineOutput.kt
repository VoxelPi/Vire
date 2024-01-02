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
}

/**
 * An output vector of a state machine.
 */
interface StateMachineOutputVector : StateMachineIOStateVector {

    /**
     * The name of the output vector.
     */
    override val name: String
}

/**
 * Creates a new output.
 */
fun output(name: String): StateMachineOutput {
    return Vire.stateMachineFactory.get().createOutput(name)
}

/**
 * Creates a new output vector.
 */
fun outputVector(name: String, initialSize: Int): StateMachineOutputVector {
    return Vire.stateMachineFactory.get().createOutputVector(name, StateMachineStateVector.InitialSizeProvider.Value(initialSize))
}

/**
 * Creates a new output vector.
 */
fun outputVector(name: String, initialSize: StateMachineParameter<Number>): StateMachineOutputVector {
    return Vire.stateMachineFactory.get().createOutputVector(name, StateMachineStateVector.InitialSizeProvider.Parameter(initialSize))
}
