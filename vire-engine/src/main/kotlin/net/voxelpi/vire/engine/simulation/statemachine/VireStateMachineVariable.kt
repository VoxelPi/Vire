package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.simulation.statemachine.StateMachineStateVector
import net.voxelpi.vire.api.simulation.statemachine.StateMachineVariable
import net.voxelpi.vire.api.simulation.statemachine.StateMachineVariableVector
import kotlin.reflect.KType

/**
 * A variable vector of a state machine.
 * @property name the name of the variable
 * @property type the type of the variable.
 * @property initialValue the initial value of the variable.
 */
data class VireStateMachineVariable<T>(
    override val name: String,
    override val type: KType,
    override val initialValue: T,
) : StateMachineVariable<T>

/**
 * A variable of a state machine.
 * @property name the name of the variable vector.
 * @property type the type of each entry of the variable vector.
 * @property initialSize the initial size of the variable vector.
 * @property initialValue the initial value of each entry of the variable vector.
 */
data class VireStateMachineVariableVector<T>(
    override val name: String,
    override val type: KType,
    override val initialSize: StateMachineStateVector.InitialSizeProvider,
    override val initialValue: T,
) : StateMachineVariableVector<T>
