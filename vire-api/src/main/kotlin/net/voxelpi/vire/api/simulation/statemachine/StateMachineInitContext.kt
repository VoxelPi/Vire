package net.voxelpi.vire.api.simulation.statemachine

/**
 * The available data during the initialization of a state machine
 */
interface StateMachineInitContext {

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
     * Sets the size of the given [vector] to [size].
     */
    fun resize(vector: StateMachineVariableVector<*>, size: Int)

    /**
     * Sets the size of the given [vector] to [size].
     */
    fun resize(vector: StateMachineInputVector, size: Int)

    /**
     * Sets the size of the given [vector] to [size].
     */
    fun resize(vector: StateMachineOutputVector, size: Int)

    /**
     * Returns the current value of the given [parameter].
     */
    operator fun <T> get(parameter: StateMachineParameter<T>): T
}
