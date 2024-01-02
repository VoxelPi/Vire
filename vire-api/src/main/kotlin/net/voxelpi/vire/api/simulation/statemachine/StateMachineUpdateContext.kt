package net.voxelpi.vire.api.simulation.statemachine

/**
 * The available data during the update of a state machine
 */
interface StateMachineUpdateContext {

    /**
     * Returns the size of the given [vector].
     */
    fun size(vector: StateMachineVariableVector<*>): Int

    /**
     * Returns the size of the given [vector].
     */
    fun size(vector: StateMachineInputVector): Int

    /**
     * Returns the size of the given [vector].
     */
    fun size(vector: StateMachineOutputVector): Int

    /**
     * Returns the current value of the given [parameter].
     */
    operator fun <T> get(parameter: StateMachineParameter<T>): T

    /**
     * Returns the current value of the given [variable].
     */
    operator fun <T> get(variable: StateMachineVariable<T>): T

    /**
     * Returns the current value of the given [vector].
     */
    operator fun <T> get(vector: StateMachineVariableVector<T>, index: Int): T
}
