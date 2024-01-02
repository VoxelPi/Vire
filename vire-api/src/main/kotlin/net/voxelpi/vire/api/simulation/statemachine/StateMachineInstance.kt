package net.voxelpi.vire.api.simulation.statemachine

import net.voxelpi.vire.api.simulation.LogicState

interface StateMachineInstance {

    /**
     * The state machine.
     */
    val stateMachine: StateMachine

    /**
     * Configures the parameters of the state machine using the given [action].
     * If the configuration is successful, meaning all new parameter values are valid,
     * `true` is returned, other `false` is returned.
     */
    fun configureParameters(action: ConfigurationContext.() -> Unit): Boolean

    fun size(input: StateMachineInput): Int

    fun size(output: StateMachineOutput): Int

    operator fun <T> get(parameter: StateMachineParameter<T>): T

    operator fun <T> get(variable: StateMachineVariable<T>): T

    operator fun <T> set(variable: StateMachineVariable<T>, value: T)

    operator fun get(input: StateMachineInput, index: Int = 0): LogicState

    fun vector(input: StateMachineInput): Array<LogicState>

    operator fun get(output: StateMachineOutput, index: Int = 0): LogicState

    fun vector(output: StateMachineOutput): Array<LogicState>

    interface ConfigurationContext {

        /**
         * The state machine.
         */
        val stateMachine: StateMachine

        /**
         * Returns the current value of the given [parameter].
         */
        operator fun <T> get(parameter: StateMachineParameter<T>): T

        /**
         * Sets the value of the given [parameter] to the given [value].
         */
        operator fun <T> set(parameter: StateMachineParameter<T>, value: T)
    }
}
