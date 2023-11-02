package net.voxelpi.vire.api.simulation.component

import kotlin.reflect.KType
import kotlin.reflect.typeOf

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
     * The type of the parameter.
     */
    val type: KType

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
    override val type: KType,
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
    override val type: KType,
    override val initialValue: T,
    val predicate: (value: T, context: StateMachineParameterContext) -> Boolean = { _, _ -> true },
) : StateMachineParameter<T> {

    /**
     * Creates a parameter, whose predicate doesn't depend on the parameter context.
     */
    constructor(name: String, type: KType, initialValue: T, predicate: (value: T) -> Boolean) :
        this(name, type, initialValue, { value, _ -> predicate(value) })

    override fun isValid(value: T, context: StateMachineParameterContext): Boolean {
        return predicate(value, context)
    }
}

/**
 * A byte parameter with the given [name] and [initialValue]. The value of the parameter must be between [min] and [max].
 */
data class StateMachineRangeParameter<T : Comparable<T>>(
    override val name: String,
    override val type: KType,
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
    override val type: KType,
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

/**
 * Creates a new parameter.
 */
inline fun <reified T> parameter(
    name: String,
    initialValue: T,
): StateMachineUnconstrainedParameter<T> {
    return StateMachineUnconstrainedParameter(name, typeOf<T>(), initialValue)
}

/**
 * Creates a new parameter.
 */
inline fun <reified T> parameter(
    name: String,
    initialValue: T,
    noinline predicate: (value: T) -> Boolean,
): StateMachinePredicateParameter<T> {
    return StateMachinePredicateParameter(name, typeOf<T>(), initialValue, predicate)
}

/**
 * Creates a new parameter.
 */
inline fun <reified T> parameter(
    name: String,
    initialValue: T,
    noinline predicate: (value: T, context: StateMachineParameterContext) -> Boolean,
): StateMachinePredicateParameter<T> {
    return StateMachinePredicateParameter(name, typeOf<T>(), initialValue, predicate)
}

/**
 * Creates a new parameter.
 */
inline fun <reified T> parameter(
    name: String,
    initialValue: T,
    values: Collection<T>,
): StateMachineSelectionParameter<T> {
    return StateMachineSelectionParameter(name, typeOf<T>(), initialValue, values)
}

/**
 * Creates a new parameter.
 */
fun parameter(
    name: String,
    initialValue: Byte,
    min: Byte = Byte.MIN_VALUE,
    max: Byte = Byte.MAX_VALUE,
): StateMachineRangeParameter<Byte> {
    return StateMachineRangeParameter(name, typeOf<Byte>(), initialValue, min, max)
}

/**
 * Creates a new parameter.
 */
fun parameter(
    name: String,
    initialValue: Short,
    min: Short = Short.MIN_VALUE,
    max: Short = Short.MAX_VALUE,
): StateMachineRangeParameter<Short> {
    return StateMachineRangeParameter(name, typeOf<Short>(), initialValue, min, max)
}

/**
 * Creates a new parameter.
 */
fun parameter(
    name: String,
    initialValue: Int,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
): StateMachineRangeParameter<Int> {
    return StateMachineRangeParameter(name, typeOf<Int>(), initialValue, min, max)
}

/**
 * Creates a new parameter.
 */
fun parameter(
    name: String,
    initialValue: Long,
    min: Long = Long.MIN_VALUE,
    max: Long = Long.MAX_VALUE,
): StateMachineRangeParameter<Long> {
    return StateMachineRangeParameter(name, typeOf<Long>(), initialValue, min, max)
}

/**
 * Creates a new parameter.
 */
fun parameter(
    name: String,
    initialValue: UByte,
    min: UByte = UByte.MIN_VALUE,
    max: UByte = UByte.MAX_VALUE,
): StateMachineRangeParameter<UByte> {
    return StateMachineRangeParameter(name, typeOf<UByte>(), initialValue, min, max)
}

/**
 * Creates a new parameter.
 */
fun parameter(
    name: String,
    initialValue: UShort,
    min: UShort = UShort.MIN_VALUE,
    max: UShort = UShort.MAX_VALUE,
): StateMachineRangeParameter<UShort> {
    return StateMachineRangeParameter(name, typeOf<UShort>(), initialValue, min, max)
}

/**
 * Creates a new parameter.
 */
fun parameter(
    name: String,
    initialValue: UInt,
    min: UInt = UInt.MIN_VALUE,
    max: UInt = UInt.MAX_VALUE,
): StateMachineRangeParameter<UInt> {
    return StateMachineRangeParameter(name, typeOf<UInt>(), initialValue, min, max)
}

/**
 * Creates a new parameter.
 */
fun parameter(
    name: String,
    initialValue: ULong,
    min: ULong = ULong.MIN_VALUE,
    max: ULong = ULong.MAX_VALUE,
): StateMachineRangeParameter<ULong> {
    return StateMachineRangeParameter(name, typeOf<ULong>(), initialValue, min, max)
}

/**
 * Creates a new parameter.
 */
fun parameter(
    name: String,
    initialValue: Float,
    min: Float = Float.MIN_VALUE,
    max: Float = Float.MAX_VALUE,
): StateMachineRangeParameter<Float> {
    return StateMachineRangeParameter(name, typeOf<Float>(), initialValue, min, max)
}

/**
 * Creates a new parameter.
 */
fun parameter(
    name: String,
    initialValue: Double,
    min: Double = Double.MIN_VALUE,
    max: Double = Double.MAX_VALUE,
): StateMachineRangeParameter<Double> {
    return StateMachineRangeParameter(name, typeOf<Double>(), initialValue, min, max)
}
