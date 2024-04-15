package net.voxelpi.vire.api.circuit.statemachine

import net.voxelpi.vire.api.Vire
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A parameter of a state machine.
 * Parameters can be used the configure the state machine as they can be accessed and modified externally.
 */
interface StateMachineParameter<T> : StateMachineState {

    /**
     * The name of the parameter.
     */
    override val name: String

    /**
     * The type of the parameter.
     */
    val type: KType

    /**
     * The initial value of the variable.
     */
    val initialValue: T

    /**
     * Returns if the variable accepts the given value.
     */
    fun isValidType(value: Any?): Boolean

    /**
     * Returns if the given [value] is valid for this parameter.
     */

    fun isValid(value: T): Boolean

    /**
     * Returns if the given [value] is valid for this parameter.
     */
    @Suppress("UNCHECKED_CAST")
    fun isValidTypeAndValue(value: Any?): Boolean {
        return isValidType(value) && isValid(value as T)
    }

    /**
     * An unconstrained parameter of a state machine.
     * Parameters can be used the configure the state machine as they can be accessed and modified externally.
     */
    interface Unconstrained<T> : StateMachineParameter<T>

    /**
     * A parameter of a state machine, the predicate defines what values are valid for the parameter.
     * Parameters can be used the configure the state machine as they can be accessed and modified externally.
     */
    interface Predicate<T> : StateMachineParameter<T> {

        /**
         * The predicate a value must satisfy to be allowed.
         */
        val predicate: (value: T) -> Boolean
    }

    /**
     * A parameter with the given [name] and [initialValue].
     * The value of the parameter must be between [min] and [max].
     */
    interface Range<T : Comparable<T>> : StateMachineParameter<T> {

        /**
         * The minimum allowed value.
         */
        val min: T

        /**
         * The maximum allowed value.
         */
        val max: T
    }

    /**
     * A parameter with the given [name] and [initialValue].
     * The value of the parameter must be an element of [possibleValues].
     */
    interface Selection<T> : StateMachineParameter<T> {

        /**
         * Allowed values of the parameters. The parameter can only be set to an element of this list.
         */
        val possibleValues: Collection<T>
    }
}

/**
 * Creates a new unconstrained parameter with the given [name] and [initialValue].
 */
inline fun <reified T> parameter(
    name: String,
    initialValue: T,
): StateMachineParameter.Unconstrained<T> {
    return Vire.get().stateMachineFactory.createUnconstrainedParameter(name, typeOf<T>(), initialValue)
}

/**
 * Creates a new unconstrained parameter with the given [name] and [initialValue].
 */
fun <T> parameter(
    name: String,
    type: KType,
    initialValue: T,
): StateMachineParameter.Unconstrained<T> {
    return Vire.get().stateMachineFactory.createUnconstrainedParameter(name, type, initialValue)
}

/**
 * Creates a new parameter with the given [name], [initialValue] and [predicate].
 */
inline fun <reified T> parameter(
    name: String,
    initialValue: T,
    noinline predicate: (value: T) -> Boolean,
): StateMachineParameter.Predicate<T> {
    return Vire.get().stateMachineFactory.createPredicateParameter(name, typeOf<T>(), initialValue, predicate)
}

/**
 * Creates a new parameter with the given [name], [initialValue] and [predicate].
 */
fun <T> parameter(
    name: String,
    type: KType,
    initialValue: T,
    predicate: (value: T) -> Boolean,
): StateMachineParameter.Predicate<T> {
    return Vire.get().stateMachineFactory.createPredicateParameter(name, type, initialValue, predicate)
}

/**
 * Creates a new selection parameter with the given [name] and [initialValue].
 * The value of the parameter must be one of [selection].
 */
inline fun <reified T> parameter(
    name: String,
    initialValue: T,
    selection: Collection<T>,
): StateMachineParameter.Selection<T> {
    return Vire.get().stateMachineFactory.createSelectionParameter(name, typeOf<T>(), initialValue, selection)
}

/**
 * Creates a new selection parameter with the given [name] and [initialValue].
 * The value of the parameter must be one of [selection].
 */
fun <T> parameter(
    name: String,
    type: KType,
    initialValue: T,
    selection: Collection<T>,
): StateMachineParameter.Selection<T> {
    return Vire.get().stateMachineFactory.createSelectionParameter(name, type, initialValue, selection)
}

/**
 * Creates a new selection parameter with the given [name] and [initialValue].
 * The value of the parameter must be one of [selection].
 */
inline fun <reified T> parameter(
    name: String,
    initialValue: T,
    vararg selection: T,
): StateMachineParameter.Selection<T> {
    return Vire.get().stateMachineFactory.createSelectionParameter(name, typeOf<T>(), initialValue, selection.toList())
}

/**
 * Creates a new selection parameter with the given [name] and [initialValue].
 * The value of the parameter must be one of [selection].
 */
fun <T> parameter(
    name: String,
    type: KType,
    initialValue: T,
    vararg selection: T,
): StateMachineParameter.Selection<T> {
    return Vire.get().stateMachineFactory.createSelectionParameter(name, type, initialValue, selection.toList())
}

/**
 * Creates a new byte parameter with the given [name] and [initialValue].
 * The value must be in the range of [min] to [max].
 */
fun parameter(
    name: String,
    initialValue: Byte,
    min: Byte = Byte.MIN_VALUE,
    max: Byte = Byte.MAX_VALUE,
): StateMachineParameter.Range<Byte> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<Byte>(), initialValue, min, max)
}

/**
 * Creates a new short with the given [name] and [initialValue].
 * The value must be in the range of [min] to [max].
 */
fun parameter(
    name: String,
    initialValue: Short,
    min: Short = Short.MIN_VALUE,
    max: Short = Short.MAX_VALUE,
): StateMachineParameter.Range<Short> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<Short>(), initialValue, min, max)
}

/**
 * Creates a new int parameter with the given [name] and [initialValue].
 * The value must be in the range of [min] to [max].
 */
fun parameter(
    name: String,
    initialValue: Int,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
): StateMachineParameter.Range<Int> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<Int>(), initialValue, min, max)
}

/**
 * Creates a new int parameter with the given [name] and [initialValue].
 * The value must be in the given [range].
 */
fun parameter(
    name: String,
    initialValue: Int,
    range: IntRange,
): StateMachineParameter.Range<Int> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<Int>(), initialValue, range.first, range.last)
}

/**
 * Creates a new long parameter with the given [name] and [initialValue].
 * The value must be in the range of [min] to [max].
 */
fun parameter(
    name: String,
    initialValue: Long,
    min: Long = Long.MIN_VALUE,
    max: Long = Long.MAX_VALUE,
): StateMachineParameter.Range<Long> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<Long>(), initialValue, min, max)
}

/**
 * Creates a new long parameter with the given [name] and [initialValue].
 * The value must be in the given [range].
 */
fun parameter(
    name: String,
    initialValue: Long,
    range: LongRange,
): StateMachineParameter.Range<Long> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<Long>(), initialValue, range.first, range.last)
}

/**
 * Creates a new unsigned byte parameter with the given [name] and [initialValue].
 * The value must be in the range of [min] to [max].
 */
fun parameter(
    name: String,
    initialValue: UByte,
    min: UByte = UByte.MIN_VALUE,
    max: UByte = UByte.MAX_VALUE,
): StateMachineParameter.Range<UByte> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<UByte>(), initialValue, min, max)
}

/**
 * Creates a new unsigned short parameter with the given [name] and [initialValue].
 * The value must be in the range of [min] to [max].
 */
fun parameter(
    name: String,
    initialValue: UShort,
    min: UShort = UShort.MIN_VALUE,
    max: UShort = UShort.MAX_VALUE,
): StateMachineParameter.Range<UShort> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<UShort>(), initialValue, min, max)
}

/**
 * Creates a new unsigned int parameter with the given [name] and [initialValue].
 * The value must be in the range of [min] to [max].
 */
fun parameter(
    name: String,
    initialValue: UInt,
    min: UInt = UInt.MIN_VALUE,
    max: UInt = UInt.MAX_VALUE,
): StateMachineParameter.Range<UInt> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<UInt>(), initialValue, min, max)
}

/**
 * Creates a new unsigned int parameter with the given [name] and [initialValue].
 * The value must be in the given [range].
 */
fun parameter(
    name: String,
    initialValue: UInt,
    range: UIntRange,
): StateMachineParameter.Range<UInt> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<UInt>(), initialValue, range.first, range.last)
}

/**
 * Creates a new unsigned long parameter with the given [name] and [initialValue].
 * The value must be in the range of [min] to [max].
 */
fun parameter(
    name: String,
    initialValue: ULong,
    min: ULong = ULong.MIN_VALUE,
    max: ULong = ULong.MAX_VALUE,
): StateMachineParameter.Range<ULong> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<ULong>(), initialValue, min, max)
}

/**
 * Creates a new unsigned long parameter with the given [name] and [initialValue].
 * The value must be in the range of [min] to [max].
 */
fun parameter(
    name: String,
    initialValue: ULong,
    range: ULongRange,
): StateMachineParameter.Range<ULong> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<ULong>(), initialValue, range.first, range.last)
}

/**
 * Creates a new float parameter with the given [name] and [initialValue].
 * The value must be in the range of [min] to [max].
 */
fun parameter(
    name: String,
    initialValue: Float,
    min: Float = Float.NEGATIVE_INFINITY,
    max: Float = Float.POSITIVE_INFINITY,
): StateMachineParameter.Range<Float> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<Float>(), initialValue, min, max)
}

/**
 * Creates a new float parameter with the given [name] and [initialValue].
 * The value must be in the given [range].
 */
fun parameter(
    name: String,
    initialValue: Float,
    range: ClosedFloatingPointRange<Float>,
): StateMachineParameter.Range<Float> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<Float>(), initialValue, range.start, range.endInclusive)
}

/**
 * Creates a new double parameter with the given [name] and [initialValue].
 * The value must be in the range of [min] to [max].
 */
fun parameter(
    name: String,
    initialValue: Double,
    min: Double = Double.NEGATIVE_INFINITY,
    max: Double = Double.POSITIVE_INFINITY,
): StateMachineParameter.Range<Double> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<Double>(), initialValue, min, max)
}

/**
 * Creates a new double parameter with the given [name] and [initialValue].
 * The value must be in the given [range].
 */
fun parameter(
    name: String,
    initialValue: Double,
    range: ClosedFloatingPointRange<Double>,
): StateMachineParameter.Range<Double> {
    return Vire.get().stateMachineFactory.createRangeParameter(name, typeOf<Double>(), initialValue, range.start, range.endInclusive)
}
