package net.voxelpi.vire.api.circuit.statemachine

import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.LogicValue
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

    /**
     * The initial state of the output.
     */
    val initialValue: LogicState
}

/**
 * Creates a new output.
 */
fun output(
    name: String,
    initialSize: Int = 1,
    initialValue: LogicState = LogicState.value(LogicValue.NONE),
): StateMachineOutput {
    return Vire.get().stateMachineFactory.createOutput(name, StateMachineIOState.InitialSizeProvider.Value(initialSize), initialValue)
}

/**
 * Creates a new output.
 */
fun output(
    name: String,
    initialSize: StateMachineParameter<out Number>,
    initialValue: LogicState = LogicState.value(LogicValue.NONE),
): StateMachineOutput {
    return Vire.get().stateMachineFactory.createOutput(name, StateMachineIOState.InitialSizeProvider.Parameter(initialSize), initialValue)
}

/**
 * Creates a new output.
 */
fun output(
    name: String,
    initialSize: StateMachineIOState.InitialSizeProvider,
    initialValue: LogicState = LogicState.value(LogicValue.NONE),
): StateMachineOutput {
    return Vire.get().stateMachineFactory.createOutput(name, initialSize, initialValue)
}
