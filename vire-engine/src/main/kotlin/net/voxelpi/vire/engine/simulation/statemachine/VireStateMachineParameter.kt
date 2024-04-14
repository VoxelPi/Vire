package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.simulation.statemachine.StateMachineParameter
import net.voxelpi.vire.engine.util.isInstanceOfType
import kotlin.reflect.KType

/**
 * A parameter of a state machine.
 * Parameters can be used the configure the state machine as they can be accessed and modified externally.
 */
interface VireStateMachineParameter<T> : StateMachineParameter<T> {

    override fun isValidType(value: Any?): Boolean {
        return isInstanceOfType(value, type)
    }

    /**
     * A parameter with the given [name] and [initialValue].
     */
    data class Unconstrained<T>(
        override val name: String,
        override val type: KType,
        override val initialValue: T,
    ) : VireStateMachineParameter<T>, StateMachineParameter.Unconstrained<T> {

        override fun isValid(value: T): Boolean {
            return true
        }
    }

    /**
     * A parameter of a state machine.
     * Parameters can be used the configure the state machine as they can be accessed and modified externally.
     *
     * @property name the name of the parameter.
     * @property initialValue the initial value of the variable.
     * @property predicate the predicate a value must satisfy to be allowed.
     */
    data class Predicate<T>(
        override val name: String,
        override val type: KType,
        override val initialValue: T,
        override val predicate: (value: T) -> Boolean = { _ -> true },
    ) : VireStateMachineParameter<T>, StateMachineParameter.Predicate<T> {

        init {
            require(isValid(initialValue)) { "Invalid initial value $initialValue." }
        }

        override fun isValid(value: T): Boolean {
            return predicate(value)
        }
    }

    /**
     * A byte parameter with the given [name] and [initialValue].
     * The value of the parameter must be between [min] and [max].
     */
    data class Range<T : Comparable<T>>(
        override val name: String,
        override val type: KType,
        override val initialValue: T,
        override val min: T,
        override val max: T,
    ) : VireStateMachineParameter<T>, StateMachineParameter.Range<T> {

        init {
            require(min <= max) { "Invalid specification, min ($min) must be less or equal max ($max)." }
            require(isValid(initialValue)) { "Invalid initial value $initialValue. Must be in [$min, $max]." }
        }

        override fun isValid(value: T): Boolean {
            return value in min..max
        }
    }

    /**
     * A parameter with the given [name] and [initialValue].
     * The value of the parameter must be an element of [possibleValues].
     */
    data class Selection<T>(
        override val name: String,
        override val type: KType,
        override val initialValue: T,
        override val possibleValues: Collection<T>,
    ) : VireStateMachineParameter<T>, StateMachineParameter.Selection<T> {

        init {
            require(isValid(initialValue)) { "Invalid initial value $initialValue. Must be in $possibleValues." }
        }

        override fun isValid(value: T): Boolean {
            return value in possibleValues
        }
    }
}
