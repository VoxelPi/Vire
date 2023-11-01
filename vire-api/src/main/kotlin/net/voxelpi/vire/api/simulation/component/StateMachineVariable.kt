package net.voxelpi.vire.api.simulation.component

/**
 * A variable of a state machine.
 * Variables allow the state machine to store state.
 *
 * @property name the name of the variable.
 * @property initialValue the initial value of the variable.
 */
data class StateMachineVariable<T>(
    val name: String,
    val initialValue: T,
)
