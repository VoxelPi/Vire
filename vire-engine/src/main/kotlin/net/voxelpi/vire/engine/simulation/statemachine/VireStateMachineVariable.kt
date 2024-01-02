package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.simulation.statemachine.StateMachineVariable
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
