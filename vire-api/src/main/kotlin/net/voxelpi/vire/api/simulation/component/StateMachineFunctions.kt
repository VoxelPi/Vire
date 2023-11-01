package net.voxelpi.vire.api.simulation.component

import net.voxelpi.vire.api.simulation.network.NetworkState

/**
 * Creates a new parameter.
 */
inline fun <reified T> parameter(
    name: String,
    initialValue: T,
): StateMachineUnconstrainedParameter<T> {
    return StateMachineUnconstrainedParameter(name, initialValue)
}

/**
 * Creates a new parameter.
 */
inline fun <reified T> parameter(
    name: String,
    initialValue: T,
    noinline predicate: (value: T) -> Boolean,
): StateMachinePredicateParameter<T> {
    return StateMachinePredicateParameter(name, initialValue, predicate)
}

/**
 * Creates a new parameter.
 */
inline fun <reified T> parameter(
    name: String,
    initialValue: T,
    noinline predicate: (value: T, context: StateMachineParameterContext) -> Boolean,
): StateMachinePredicateParameter<T> {
    return StateMachinePredicateParameter(name, initialValue, predicate)
}

/**
 * Creates a new parameter.
 */
inline fun <reified T> parameter(
    name: String,
    initialValue: T,
    values: Collection<T>,
): StateMachineSelectionParameter<T> {
    return StateMachineSelectionParameter(name, initialValue, values)
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
    return StateMachineRangeParameter(name, initialValue, min, max)
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
    return StateMachineRangeParameter(name, initialValue, min, max)
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
    return StateMachineRangeParameter(name, initialValue, min, max)
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
    return StateMachineRangeParameter(name, initialValue, min, max)
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
    return StateMachineRangeParameter(name, initialValue, min, max)
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
    return StateMachineRangeParameter(name, initialValue, min, max)
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
    return StateMachineRangeParameter(name, initialValue, min, max)
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
    return StateMachineRangeParameter(name, initialValue, min, max)
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
    return StateMachineRangeParameter(name, initialValue, min, max)
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
    return StateMachineRangeParameter(name, initialValue, min, max)
}

/**
 * Creates a new variable.
 */
inline fun <reified T> variable(name: String, initialValue: T): StateMachineVariable<T> {
    return StateMachineVariable(name, initialValue)
}

/**
 * Creates a new input.
 */
fun input(name: String, initialSize: Int = 1): StateMachineInput {
    return StateMachineInput(name, initialSize)
}

/**
 * Creates a new output.
 */
fun output(name: String, initialSize: Int = 1, initialValue: NetworkState = NetworkState.None): StateMachineOutput {
    return StateMachineOutput(name, initialSize, initialValue)
}
