package net.voxelpi.vire.api.simulation.statemachine

/**
 * The available data during the initialization of a state machine
 */
interface StateMachineConfigureContext {

    /**
     * Returns the size of the given [vector].
     */
    fun size(vector: StateMachineInput): Int

    /**
     * Returns the size of the given [vector].
     */
    fun size(vector: StateMachineOutput): Int

    /**
     * Sets the size of the given [vector] to [size].
     */
    fun resize(vector: StateMachineInput, size: Int)

    /**
     * Sets the size of the given [vector] to [size].
     */
    fun resize(vector: StateMachineOutput, size: Int)

    /**
     * Returns the current value of the given [parameter].
     */
    operator fun <T> get(parameter: StateMachineParameter<T>): T
}
