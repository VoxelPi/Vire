package net.voxelpi.vire.api.simulation.statemachine

import net.voxelpi.vire.api.Vire
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.LogicValue

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
    return Vire.stateMachineFactory.get().createOutput(name, StateMachineIOState.InitialSizeProvider.Value(initialSize), initialValue)
}

/**
 * Creates a new output.
 */
fun output(
    name: String,
    initialSize: StateMachineParameter<out Number>,
    initialValue: LogicState = LogicState.value(LogicValue.NONE),
): StateMachineOutput {
    return Vire.stateMachineFactory.get().createOutput(name, StateMachineIOState.InitialSizeProvider.Parameter(initialSize), initialValue)
}
