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
}

/**
 * A variable vector of a state machine.
 */
interface StateMachineVariableVector<T> : StateMachineIOStateVector {

    /**
     * The name of the variable vector.
     */
    override val name: String

    /**
     * The type of each entry of the variable vector.
     */
    val type: KType

    /**
     * The initial value of each entry of the variable vector.
     */
    val initialValue: T
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

/**
 * Creates a new variable vector.
 */
inline fun <reified T> variableVector(
    name: String,
    initialSize: Int,
    initialValue: T,
): StateMachineVariableVector<T> {
    return Vire.stateMachineFactory.get().createVariableVector(
        name,
        typeOf<T>(),
        StateMachineStateVector.InitialSizeProvider.Value(initialSize),
        initialValue,
    )
}

/**
 * Creates a new variable vector.
 */
inline fun <reified T> variableVector(
    name: String,
    initialSize: StateMachineParameter<Number>,
    initialValue: T,
): StateMachineVariableVector<T> {
    return Vire.stateMachineFactory.get().createVariableVector(
        name,
        typeOf<T>(),
        StateMachineStateVector.InitialSizeProvider.Parameter(initialSize),
        initialValue,
    )
}
