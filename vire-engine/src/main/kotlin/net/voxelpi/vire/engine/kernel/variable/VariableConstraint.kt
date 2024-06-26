package net.voxelpi.vire.engine.kernel.variable

public interface VariableConstraint<in T> {

    /**
     * Returns if the given [value] satisfies the constraint.
     */
    public fun isValidValue(value: T): Boolean

    /**
     * A constraint that is always valid.
     */
    public data object Always : VariableConstraint<kotlin.Any?> {

        override fun isValidValue(value: kotlin.Any?): Boolean = true
    }

    /**
     * A constraint that is always valid.
     */
    public data object Never : VariableConstraint<kotlin.Any?> {

        override fun isValidValue(value: kotlin.Any?): Boolean = false
    }

    /**
     * A constraint that requires values where the given predicate returns true.
     *
     * @property predicate the predicate for the possible values.
     */
    public data class Predicate<T>(
        val predicate: (value: T) -> Boolean,
    ) : VariableConstraint<T> {

        override fun isValidValue(value: T): Boolean = predicate(value)
    }

    /**
     * A constraint that requires that the value is one of the provided possible values.
     *
     * @property possibleValues all values that are valid.
     */
    public data class Selection<T>(
        val possibleValues: Collection<T>,
    ) : VariableConstraint<T> {

        override fun isValidValue(value: T): Boolean = value in possibleValues
    }

    /**
     * A constraint that requires that the value is greater than min and less than max.
     *
     * @property min the minimum value.
     * @property max the maximum value.
     */
    public data class Range<T : Comparable<T>>(
        val min: T,
        val max: T,
    ) : VariableConstraint<T> {

        init {
            require(min <= max) { "min ($min) must be less than max ($max)" }
        }

        override fun isValidValue(value: T): Boolean = value in min..max
    }

    /**
     * A constraint that requires that the value is greater or equal to the min value.
     *
     * @property min the minimum value.
     * @property max the maximum value.
     */
    public data class Min<T : Comparable<T>>(
        val min: T,
    ) : VariableConstraint<T> {

        override fun isValidValue(value: T): Boolean = value >= min
    }

    /**
     * A constraint that requires that the value is smaller or equal to the max value.
     *
     * @property max the maximum value.
     */
    public data class Max<T : Comparable<T>>(
        val max: T,
    ) : VariableConstraint<T> {

        override fun isValidValue(value: T): Boolean = value <= max
    }

    /**
     * A constraint that requires that the value is valid for at least one of its child constraints.
     *
     * @property constraints the list of constraints.
     */
    public data class Any<T> internal constructor(
        val constraints: List<VariableConstraint<T>>,
    ) : VariableConstraint<T> {

        override fun isValidValue(value: T): Boolean = constraints.any { it.isValidValue(value) }
    }

    /**
     * A constraint that requires that the value is valid for all of its child constraints.
     *
     * @property constraints the list of constraints.
     */
    public data class All<T> internal constructor(
        val constraints: List<VariableConstraint<T>>,
    ) : VariableConstraint<T> {

        override fun isValidValue(value: T): Boolean = constraints.all { it.isValidValue(value) }
    }
}

/**
 * Creates a new predicate-constraint with the given [lambda].
 */
public fun <T> predicate(lambda: (value: T) -> Boolean): VariableConstraint.Predicate<T> {
    return VariableConstraint.Predicate(lambda)
}

/**
 * Creates a new selection-constraint with the given [possibleValues].
 */
public fun <T> inSelection(possibleValues: Collection<T>): VariableConstraint.Selection<T> {
    return VariableConstraint.Selection(possibleValues)
}

/**
 * Creates a new selection-constraint with the given [possibleValues].
 */
public fun <T> inSelection(vararg possibleValues: T): VariableConstraint.Selection<T> {
    return VariableConstraint.Selection(possibleValues.toList())
}

/**
 * Creates a new any-constraint with the given [constraints].
 * To minimize the number of constraints, the constraint is flattened if possible.
 * That means that if no constraints are defined by the lambda, a [VariableConstraint.Never] is returned and
 * if only one constrained is defined, this constrained is returned directly.
 * Otherwise, a [VariableConstraint.Any] is created with all the defined constraints.
 */
public fun <T> anyOf(vararg constraints: VariableConstraint<T>): VariableConstraint<T> {
    return when (constraints.size) {
        0 -> VariableConstraint.Never
        1 -> constraints.first()
        else -> VariableConstraint.Any(constraints.toList())
    }
}

/**
 * Creates a new all-constraint with the given [constraints].
 * To minimize the number of constraints, the constraint is flattened if possible.
 * That means that if no constraints are defined by the lambda, a [VariableConstraint.Always] is returned and
 * if only one constrained is defined, this constrained is returned directly.
 * Otherwise, a [VariableConstraint.All] is created with all the defined constraints.
 */
public fun <T> allOf(vararg constraints: VariableConstraint<T>): VariableConstraint<T> {
    return when (constraints.size) {
        0 -> VariableConstraint.Always
        1 -> constraints.first()
        else -> VariableConstraint.All(constraints.toList())
    }
}

/**
 * Creates a new min-constraint with the given [min] value.
 */
public fun <T : Comparable<T>> atLeast(min: T): VariableConstraint.Min<T> {
    return VariableConstraint.Min(min)
}

/**
 * Creates a new max-constraint with the given [max] value.
 */
public fun <T : Comparable<T>> atMost(max: T): VariableConstraint.Max<T> {
    return VariableConstraint.Max(max)
}

/**
 * Creates a new max-constraint with the given [max] value.
 */
public fun <T : Comparable<T>> inRange(min: T, max: T): VariableConstraint.Range<T> {
    return VariableConstraint.Range(min, max)
}

/**
 * Creates a new range constraint with the given [min] and [max] values.
 */
public fun <T : Comparable<T>> inRange(range: ClosedRange<T>): VariableConstraint.Range<T> {
    return VariableConstraint.Range(range.start, range.endInclusive)
}
