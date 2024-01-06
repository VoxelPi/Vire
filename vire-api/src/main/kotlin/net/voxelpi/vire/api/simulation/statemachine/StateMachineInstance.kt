package net.voxelpi.vire.api.simulation.statemachine

import net.voxelpi.vire.api.simulation.LogicState
import kotlin.reflect.KType

/**
 * An instance of a state machine.
 * Stores the state of the instance.
 */
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

    /**
     * Returns the size of the input / output with the given [name].
     */
    fun size(name: String): Int

    /**
     * Returns the size of the given [input].
     */
    fun size(input: StateMachineInput): Int

    /**
     * Returns the size of the given [output].
     */
    fun size(output: StateMachineOutput): Int

    /**
     * Returns if the state machine has a state variable with the given [name].
     */
    fun has(name: String): Boolean

    /**
     * Returns the type of the state variable with the given [name].
     * If the state variable is an input / output, the [LogicState] type is returned.
     */
    fun typeOf(name: String): KType

    /**
     * Returns the value of the state variable with the given [name]-
     * If the state variable is an input / output, the state at index 0 is returned.
     */
    operator fun get(name: String): Any?

    /**
     * Returns the value of the input / output with the given [name] at the given [index].
     */
    operator fun get(name: String, index: Int): LogicState

    /**
     * Returns the value of the complete input / output vector with the given [name].
     */
    fun vector(name: String): Array<LogicState>

    /**
     * Returns the value of the given [parameter].
     */
    operator fun <T> get(parameter: StateMachineParameter<T>): T

    /**
     * Returns the value of the given [variable].
     */
    operator fun <T> get(variable: StateMachineVariable<T>): T

    /**
     * Sets the value of the given [variable] to [value].
     */
    operator fun <T> set(variable: StateMachineVariable<T>, value: T)

    /**
     * Returns the value of the given [input] at the given [index].
     */
    operator fun get(input: StateMachineInput, index: Int = 0): LogicState

    /**
     * Returns the value of the given [input] vector.
     */
    fun vector(input: StateMachineInput): Array<LogicState>

    /**
     * Returns the value of the given [output] at the given [index].
     */
    operator fun get(output: StateMachineOutput, index: Int = 0): LogicState

    /**
     * Returns the value of the given [output] vector.
     */
    fun vector(output: StateMachineOutput): Array<LogicState>

    /**
     * The
     */
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

        /**
         * Returns the current value of the parameter with the given [parameterName].
         */
        operator fun get(parameterName: String): Any?

        /**
         * Sets the value of the parameter with the given [parameterName] to the given [value].
         */
        operator fun set(parameterName: String, value: Any?)
    }
}
