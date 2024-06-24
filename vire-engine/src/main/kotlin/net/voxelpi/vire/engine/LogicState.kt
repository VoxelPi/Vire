package net.voxelpi.vire.engine

import kotlin.math.max

public data class LogicState(
    val channels: Array<LogicValue>,
) {

    public constructor(size: Int, init: (Int) -> LogicValue) : this(Array(size, init))

    /**
     * The number of channels of the state.
     */
    val size: Int
        get() = channels.size

    public operator fun get(index: Int): LogicValue {
        if (index !in channels.indices) {
            throw IndexOutOfBoundsException(index)
        }
        return channels[index]
    }

    public fun channelOrNone(index: Int): LogicValue {
        if (index < 0) {
            throw IndexOutOfBoundsException(index)
        }
        if (index >= channels.size) {
            return LogicValue.NONE
        }
        return channels[index]
    }

    public operator fun set(index: Int, value: LogicValue) {
        if (index !in channels.indices) {
            throw IndexOutOfBoundsException(index)
        }
        channels[index] = value
    }

    public operator fun set(index: Int, value: Boolean) {
        if (index !in channels.indices) {
            throw IndexOutOfBoundsException(index)
        }
        channels[index] = if (value) LogicValue.TRUE else LogicValue.FALSE
    }

    /**
     * Creates a resized copy with the given [size].
     * If [size] is greater than the current size, the remaining entries are filled with NONE.
     */
    public fun resizedCopy(size: Int): LogicState {
        return LogicState(size) { index ->
            if (index < channels.size) channels[index] else LogicValue.NONE
        }
    }

    /**
     * Returns true if the state has at least one channel that is TRUE.
     */
    public fun toBoolean(): Boolean {
        return channels.isNotEmpty() && channels.any { it == LogicValue.TRUE }
    }

    /**
     * Generates a [BooleanState] from this logic state.
     */
    public fun booleanState(): BooleanState {
        return BooleanState(channels.size) { index ->
            channels[index] == LogicValue.TRUE
        }
    }

    /**
     * Generates a [BooleanState] from this logic state with the given [size].
     * If [size] is greater than the size of this state, the remaining channels are filled with false.
     */
    public fun booleanState(size: Int): BooleanState {
        return BooleanState(size) { index ->
            index < channels.size && channels[index] == LogicValue.TRUE
        }
    }

    public fun clone(): LogicState {
        return LogicState(channels.clone())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LogicState

        return channels.contentEquals(other.channels)
    }

    override fun hashCode(): Int {
        return channels.contentHashCode()
    }

    public operator fun plus(other: LogicState): LogicState {
        return merge(this, other)
    }

    public companion object {

        public val EMPTY: LogicState = LogicState(0) { LogicValue.NONE }

        public fun value(value: LogicValue, size: Int = 1): LogicState {
            return LogicState(size) { value }
        }

        public fun value(value: Boolean?, size: Int = 1): LogicState {
            return LogicState(size) { value.logicValue() }
        }

        /**
         * Merge the two logic states [state1] and [state2].
         */
        public fun merge(state1: LogicState, state2: LogicState): LogicState {
            val channelNumber = max(state1.channels.size, state2.channels.size)
            val channels: Array<LogicValue> = Array(channelNumber) { channelIndex ->
                val value1 = state1.channelOrNone(channelIndex)
                val value2 = state2.channelOrNone(channelIndex)
                LogicValue.merge(value1, value2)
            }
            return LogicState(channels)
        }

        /**
         * Calculates a new logic state using a bitwise operation with [state1] and [state2].
         */
        public fun bitwiseBiFunction(state1: LogicState, state2: LogicState, function: (LogicValue, LogicValue) -> LogicValue): LogicState {
            // Calculate the number of channels.
            val size = max(state1.size, state2.size)

            // Construct the new state.
            return LogicState(size) { index ->
                function(state1[index], state2[index])
            }
        }
    }
}

public fun Array<LogicState>.booleanStates(): Array<BooleanState> {
    return Array(size) { index ->
        this[index].booleanState()
    }
}

/**
 * Generates a new logic state with the given [size], where each channel is set to [value].
 */
public fun logicState(value: LogicValue, size: Int): LogicState {
    return LogicState(size) { value }
}

/**
 * Generates a new logic state with the given [values].
 */
public fun logicState(vararg values: LogicValue): LogicState {
    return LogicState(arrayOf(*values))
}

/**
 * Generates a new logic state with the given [size], where each channel is set to [value].
 */
public fun logicState(value: Boolean?, size: Int): LogicState {
    return LogicState(size) { value.logicValue() }
}

/**
 * Generates a new logic state with the given [values].
 */
public fun logicState(vararg values: Boolean?): LogicState {
    return LogicState(values.map { it.logicValue() }.toTypedArray())
}
