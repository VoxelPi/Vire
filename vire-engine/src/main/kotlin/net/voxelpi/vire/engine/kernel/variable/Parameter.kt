package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.util.isInstanceOfType
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

public interface Parameter<T> : ScalarVariable<T> {

    override val name: String

    override val type: KType

    public val initialization: VariableInitialization<T>

    /**
     * Returns if the given [value] is valid for the parameter.
     */
    public fun isValidValue(value: T): Boolean

    /**
     * Returns if the given [type] is valid for the parameter.
     */
    public fun isValidType(type: KType): Boolean {
        return type.isSubtypeOf(this.type)
    }

    /**
     * Returns if the given [value] is valid for the parameter.
     */
    @Suppress("UNCHECKED_CAST")
    public fun isValidTypeAndValue(value: Any?): Boolean {
        if (!isInstanceOfType(value, type)) {
            return false
        }
        return isValidValue(value as T)
    }

    /**
     * A parameter that has no constraints for its value.
     *
     * @property name the name of the parameter.
     * @property type the type of the parameter.
     * @property initialization the initialization of the parameter.
     */
    public data class Unconstrained<T> internal constructor(
        override val name: String,
        override val type: KType,
        override val initialization: VariableInitialization<T>,
    ) : Parameter<T> {

        override fun isValidValue(value: T): Boolean = true
    }

    /**
     * A parameter whose values are constrained to these where the given predicate returns true.
     *
     * @property name the name of the parameter.
     * @property type the type of the parameter.
     * @property initialization the initialization of the parameter.
     * @property predicate the predicate for the possible values of the parameter.
     */
    public data class Predicate<T> internal constructor(
        override val name: String,
        override val type: KType,
        override val initialization: VariableInitialization<T>,
        val predicate: (value: T) -> Boolean,
    ) : Parameter<T> {

        override fun isValidValue(value: T): Boolean = predicate(value)
    }

    /**
     * A parameter whose value must be one of the provided possible values.
     *
     * @property name the name of the parameter.
     * @property type the type of the parameter.
     * @property initialization the initialization of the parameter.
     * @property possibleValues all values that are valid for the parameter.
     */
    public data class Selection<T> internal constructor(
        override val name: String,
        override val type: KType,
        override val initialization: VariableInitialization<T>,
        val possibleValues: Collection<T>,
    ) : Parameter<T> {

        override fun isValidValue(value: T): Boolean {
            return value in possibleValues
        }
    }

    /**
     * A parameter whose value are constrained to be greater than min and less than max.
     *
     * @property name the name of the parameter.
     * @property type the type of the parameter.
     * @property initialization the initialization of the parameter.
     * @property min the minimum value of the parameter.
     * @property max the maximum value of the parameter.
     */
    public data class Range<T : Comparable<T>> internal constructor(
        override val name: String,
        override val type: KType,
        override val initialization: VariableInitialization<T>,
        val min: T,
        val max: T,
    ) : Parameter<T> {

        init {
            require(min <= max) { "min ($min) must be less than max ($max)" }
        }

        override fun isValidValue(value: T): Boolean {
            return value in min..max
        }
    }
}

/**
 * Creates a new unconstrained parameter with the given [name] and [initialization].
 */
public inline fun <reified T> parameter(
    name: String,
    initialization: VariableInitialization<T>,
): Parameter.Unconstrained<T> = parameter(name, typeOf<T>(), initialization)

/**
 * Creates a new unconstrained parameter with the given [name], [type] and [initialization].
 */
public fun <T> parameter(
    name: String,
    type: KType,
    initialization: VariableInitialization<T>,
): Parameter.Unconstrained<T> = Parameter.Unconstrained(name, type, initialization)

/**
 * Creates a new predicate parameter with the given [name], [initialization] and [predicate].
 */
public inline fun <reified T> parameter(
    name: String,
    initialization: VariableInitialization<T>,
    noinline predicate: (value: T) -> Boolean,
): Parameter.Predicate<T> = parameter(name, typeOf<T>(), initialization, predicate)

/**
 * Creates a new predicate parameter with the given [name], [type], [initialization] and [predicate].
 */
public fun <T> parameter(
    name: String,
    type: KType,
    initialization: VariableInitialization<T>,
    predicate: (value: T) -> Boolean,
): Parameter.Predicate<T> = Parameter.Predicate(name, type, initialization, predicate)

/**
 * Creates a new selection parameter with the given [name], [initialization] and [possibleValues].
 */
public inline fun <reified T> parameter(
    name: String,
    initialization: VariableInitialization<T>,
    possibleValues: Collection<T>,
): Parameter.Selection<T> = parameter(name, typeOf<T>(), initialization, possibleValues)

/**
 * Creates a new selection parameter with the given [name], [initialization] and [possibleValues].
 */
public inline fun <reified T> parameter(
    name: String,
    initialization: VariableInitialization<T>,
    vararg possibleValues: T,
): Parameter.Selection<T> = parameter(name, typeOf<T>(), initialization, possibleValues.toList())

/**
 * Creates a new selection parameter with the given [name], [type], [initialization] and [possibleValues].
 */
public fun <T> parameter(
    name: String,
    type: KType,
    initialization: VariableInitialization<T>,
    possibleValues: Collection<T>,
): Parameter.Selection<T> = Parameter.Selection(name, type, initialization, possibleValues)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [min] and [max].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<Byte>,
    min: Byte = Byte.MIN_VALUE,
    max: Byte = Byte.MAX_VALUE,
): Parameter.Range<Byte> = Parameter.Range(name, typeOf<Byte>(), initialization, min, max)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [min] and [max].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<UByte>,
    min: UByte = UByte.MIN_VALUE,
    max: UByte = UByte.MAX_VALUE,
): Parameter.Range<UByte> = Parameter.Range(name, typeOf<Byte>(), initialization, min, max)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [min] and [max].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<Short>,
    min: Short = Short.MIN_VALUE,
    max: Short = Short.MAX_VALUE,
): Parameter.Range<Short> = Parameter.Range(name, typeOf<Short>(), initialization, min, max)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [min] and [max].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<UShort>,
    min: UShort = UShort.MIN_VALUE,
    max: UShort = UShort.MAX_VALUE,
): Parameter.Range<UShort> = Parameter.Range(name, typeOf<Byte>(), initialization, min, max)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [min] and [max].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<Int>,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
): Parameter.Range<Int> = Parameter.Range(name, typeOf<Int>(), initialization, min, max)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [range].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<Int>,
    range: IntRange,
): Parameter.Range<Int> = Parameter.Range(name, typeOf<Int>(), initialization, range.first, range.last)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [min] and [max].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<UInt>,
    min: UInt = UInt.MIN_VALUE,
    max: UInt = UInt.MAX_VALUE,
): Parameter.Range<UInt> = Parameter.Range(name, typeOf<Byte>(), initialization, min, max)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [range].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<UInt>,
    range: UIntRange,
): Parameter.Range<UInt> = Parameter.Range(name, typeOf<UInt>(), initialization, range.first, range.last)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [min] and [max].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<Long>,
    min: Long = Long.MIN_VALUE,
    max: Long = Long.MAX_VALUE,
): Parameter.Range<Long> = Parameter.Range(name, typeOf<Long>(), initialization, min, max)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [range].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<Long>,
    range: LongRange,
): Parameter.Range<Long> = Parameter.Range(name, typeOf<Long>(), initialization, range.first, range.last)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [min] and [max].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<ULong>,
    min: ULong = ULong.MIN_VALUE,
    max: ULong = ULong.MAX_VALUE,
): Parameter.Range<ULong> = Parameter.Range(name, typeOf<Byte>(), initialization, min, max)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [range].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<ULong>,
    range: ULongRange,
): Parameter.Range<ULong> = Parameter.Range(name, typeOf<ULong>(), initialization, range.first, range.last)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [min] and [max].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<Float>,
    min: Float = Float.NEGATIVE_INFINITY,
    max: Float = Float.POSITIVE_INFINITY,
): Parameter.Range<Float> = Parameter.Range(name, typeOf<Float>(), initialization, min, max)

// TODO: Platform clash
// /**
// * Creates a new range parameter with the given [name] and [initialization], with the given [range].
// */
// public fun parameter(
//    name: String,
//    initialization: VariableInitialization<Float>,
//    range: ClosedFloatingPointRange<Float>
// ): Parameter.Range<Float> = Parameter.Range(name, typeOf<Float>(), initialization, range.start, range.endInclusive)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [min] and [max].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<Double>,
    min: Double = Double.NEGATIVE_INFINITY,
    max: Double = Double.POSITIVE_INFINITY,
): Parameter.Range<Double> = Parameter.Range(name, typeOf<Double>(), initialization, min, max)

/**
 * Creates a new range parameter with the given [name] and [initialization], with the given [range].
 */
public fun parameter(
    name: String,
    initialization: VariableInitialization<Double>,
    range: ClosedFloatingPointRange<Double>,
): Parameter.Range<Double> = Parameter.Range(name, typeOf<Double>(), initialization, range.start, range.endInclusive)
