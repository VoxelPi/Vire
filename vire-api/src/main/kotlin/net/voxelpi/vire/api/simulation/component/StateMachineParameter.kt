package net.voxelpi.vire.api.simulation.component

/**
 * A parameter of a state machine.
 * Parameters can be used the configure the state machine as they can be accessed and modified externally.
 */
interface StateMachineParameter<T> {

    /**
     * The name of the parameter.
     */
    val name: String

    /**
     * The initial value of the variable.
     */
    val initialValue: T

    /**
     * Returns if the given [value] is valid for this parameter in the given [context].
     */
    fun isValid(value: T, context: StateMachineParameterContext): Boolean
}

/**
 * A parameter with the given [name] and [initialValue].
 */
data class StateMachineUnconstrainedParameter<T>(
    override val name: String,
    override val initialValue: T,
) : StateMachineParameter<T> {

    override fun isValid(value: T, context: StateMachineParameterContext): Boolean {
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
data class StateMachinePredicateParameter<T>(
    override val name: String,
    override val initialValue: T,
    val predicate: (value: T, context: StateMachineParameterContext) -> Boolean = { _, _ -> true },
) : StateMachineParameter<T> {

    /**
     * Creates a parameter, whose predicate doesn't depend on the parameter context.
     */
    constructor(name: String, initialValue: T, predicate: (value: T) -> Boolean) :
        this(name, initialValue, { value, _ -> predicate(value) })

    override fun isValid(value: T, context: StateMachineParameterContext): Boolean {
        return predicate(value, context)
    }
}

/**
 * A byte parameter with the given [name] and [initialValue]. The value of the parameter must be between [min] and [max].
 */
data class StateMachineRangeParameter<T : Comparable<T>>(
    override val name: String,
    override val initialValue: T,
    val min: T,
    val max: T,
) : StateMachineParameter<T> {

    init {
        require(min <= max)
    }

    override fun isValid(value: T, context: StateMachineParameterContext): Boolean {
        return value in min..max
    }
}

/**
 * A parameter with the given [name] and [initialValue]. The value of the parameter must be an element of [possibleValues].
 */
data class StateMachineSelectionParameter<T>(
    override val name: String,
    override val initialValue: T,
    val possibleValues: Collection<T>,
) : StateMachineParameter<T> {

    init {
        require(initialValue in possibleValues)
    }

    override fun isValid(value: T, context: StateMachineParameterContext): Boolean {
        return value in possibleValues
    }
}
