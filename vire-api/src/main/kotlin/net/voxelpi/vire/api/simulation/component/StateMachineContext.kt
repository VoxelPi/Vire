package net.voxelpi.vire.api.simulation.component

import net.voxelpi.vire.api.simulation.network.NetworkState

/**
 * The context of a state machine
 */
interface StateMachineContext {

    /**
     * The state machine.
     */
    val stateMachine: StateMachine

    /**
     * Gets the value of the given [variable]].
     */
    operator fun <T> get(variable: StateMachineVariable<T>): T

    /**
     * Sets the value of the given [variable] to [value].
     */
    operator fun <T> set(variable: StateMachineVariable<T>, value: T)

    /**
     * Gets the value of the given [input].
     */
    fun vector(input: StateMachineInput): Array<NetworkState>

    /**
     * Gets the value at the given [index] of the given [input].
     */
    operator fun get(input: StateMachineInput, index: Int = 0): NetworkState

    /**
     * Gets the value of the given [output].
     */
    fun vector(output: StateMachineOutput): Array<NetworkState>

    /**
     * Gets the value at the given [index] of the given [output].
     */
    operator fun get(output: StateMachineOutput, index: Int = 0): NetworkState

    /**
     * Sets the value at the given [output] to [value].
     */
    fun vector(output: StateMachineOutput, value: Array<NetworkState>)

    /**
     * Sets the value at the given [index] of the given [output] to [value].
     */
    operator fun set(output: StateMachineOutput, index: Int = 0, value: NetworkState)

    /**
     * Changes the number of variables of the given [input] to [size].
     */
    fun resize(input: StateMachineInput, size: Int)

    /**
     * Changes the number of variables of the given [output] to [size].
     */
    fun resize(output: StateMachineOutput, size: Int)

    /**
     * Returns the current number of variable, of the given input.
     */
    fun size(input: StateMachineInput): Int

    /**
     * Returns the current number of variables, of the given output.
     */
    fun size(output: StateMachineOutput): Int
}
