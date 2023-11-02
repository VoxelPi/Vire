package net.voxelpi.vire.api.simulation.component

import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A variable of a state machine.
 * Variables allow the state machine to store state.
 *
 * @property name the name of the variable.
 * @property type the type of the variable.
 * @property initialValue the initial value of the variable.
 */
data class StateMachineVariable<T>(
    val name: String,
    val type: KType,
    val initialValue: T,
)

/**
 * Creates a new variable.
 */
inline fun <reified T> variable(name: String, initialValue: T): StateMachineVariable<T> {
    return StateMachineVariable(name, typeOf<T>(), initialValue)
}
