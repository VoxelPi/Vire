package net.voxelpi.vire.engine.circuit.statemachine

import net.voxelpi.vire.api.circuit.statemachine.StateMachineVariable
import net.voxelpi.vire.engine.util.isInstanceOfType
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
) : StateMachineVariable<T> {

    override fun isValidType(value: Any?): Boolean {
        return isInstanceOfType(value, type)
    }
}
