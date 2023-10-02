package net.voxelpi.vire.api.simulation.component

/**
 * A variable of a state machine.
 *
 * @property name the name of the input.
 * @property public if the public can be modified outside the state machine. If a public variable is changed, the init function is called.
 * @property initialValue the initial value of the variable.
 */
data class StateMachineVariable<T>(
    val name: String,
    val public: Boolean,
    val initialValue: T?,
) {

    companion object {
        /**
         * Creates a new public variable with the given [name] and [initialValue].
         * Public variables can be modified from outside the state machine. They are intended to be used as parameters of the state machine.
         */
        inline fun <reified T> public(name: String, initialValue: T? = null): StateMachineVariable<T> {
            return StateMachineVariable(name, true, initialValue)
        }

        /**
         * Creates a new private variable with the given [name] and [initialValue].
         * Private variables can only be modified by the state machine. They are intended to be used for internal calculation and
         * to store data between ticks.
         */
        inline fun <reified T> private(name: String, initialValue: T? = null): StateMachineVariable<T> {
            return StateMachineVariable(name, false, initialValue)
        }
    }
}
