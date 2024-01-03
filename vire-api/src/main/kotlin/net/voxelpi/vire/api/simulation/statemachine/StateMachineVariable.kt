package net.voxelpi.vire.api.simulation.statemachine

import net.voxelpi.vire.api.Vire
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A variable of a state machine.
 */
interface StateMachineVariable<T> : StateMachineState {

    /**
     * The name of the variable.
     */
    override val name: String

    /**
     * The type of the variable.
     */
    val type: KType

    /**
     * The initial value of the variable.
     */
    val initialValue: T

    /**
     * Returns if the variable accepts the given value.
     */
    fun acceptsValue(value: Any?): Boolean
}

/**
 * Creates a new variable.
 */
inline fun <reified T> variable(
    name: String,
    initialValue: T,
): StateMachineVariable<T> {
    return Vire.stateMachineFactory.get().createVariable(
        name,
        typeOf<T>(),
        initialValue,
    )
}
