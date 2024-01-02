package net.voxelpi.vire.api.simulation.statemachine

import net.voxelpi.vire.api.simulation.LogicState

/**
 * The available data during the update of a state machine
 */
interface StateMachineUpdateContext {

    /**
     * The state machine.
     */
    val stateMachine: StateMachine

    /**
     * Returns the size of the given [input].
     */
    fun size(input: StateMachineInput): Int

    /**
     * Returns the size of the given [output].
     */
    fun size(output: StateMachineOutput): Int

    /**
     * Returns the current value of the given [parameter].
     */
    operator fun <T> get(parameter: StateMachineParameter<T>): T

    /**
     * Returns the current value of the given [variable].
     */
    operator fun <T> get(variable: StateMachineVariable<T>): T

    /**
     * Sets the current value of the given [variable] to the given [value].
     */
    operator fun <T> set(variable: StateMachineVariable<T>, value: T)

    /**
     * Returns the current value of the given [input] at index [index].
     */
    operator fun get(input: StateMachineInput, index: Int = 0): LogicState

    /**
     * Sets the value of the given [output] at index [index] to the given [value].
     */
    operator fun set(output: StateMachineOutput, index: Int = 0, value: LogicState)
}
