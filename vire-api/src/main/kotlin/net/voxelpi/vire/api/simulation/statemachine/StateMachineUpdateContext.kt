package net.voxelpi.vire.api.simulation.statemachine

import net.voxelpi.vire.api.simulation.LogicState

/**
 * The available data during the update of a state machine
 */
interface StateMachineUpdateContext {

    /**
     * Returns the size of the given [vector].
     */
    fun size(vector: StateMachineInput): Int

    /**
     * Returns the size of the given [vector].
     */
    fun size(vector: StateMachineOutput): Int

    /**
     * Returns the current value of the given [parameter].
     */
    operator fun <T> get(parameter: StateMachineParameter<T>): T

    /**
     * Returns the current value of the given [variable].
     */
    operator fun <T> get(variable: StateMachineVariable<T>): T

    /**
     * Returns the current value of the given [input] at index [index].
     */
    operator fun get(input: StateMachineInput, index: Int = 0): LogicState

    /**
     * Returns the current value of the given [output] at index [index] .
     */
    operator fun get(output: StateMachineOutput, index: Int = 0): LogicState

    /**
     * Sets the value of the given [output] at index [index] to the given [value].
     */
    operator fun set(output: StateMachineOutput, index: Int = 0, value: LogicState)
}
