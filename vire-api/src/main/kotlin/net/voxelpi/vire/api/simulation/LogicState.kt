package net.voxelpi.vire.api.simulation

import kotlin.math.max

data class LogicState(
    val channels: Array<Value>,
) {

    constructor(size: Int, init: (Int) -> Value) : this(Array(size, init))

    operator fun get(index: Int): Value {
        if (index !in channels.indices) {
            throw IndexOutOfBoundsException(index)
        }
        return channels[index]
    }

    fun channelOrNone(index: Int): Value {
        if (index < 0) {
            throw IndexOutOfBoundsException(index)
        }
        if (index >= channels.size) {
            return Value.NONE
        }
        return channels[index]
    }

    operator fun set(index: Int, value: Value) {
        if (index !in channels.indices) {
            throw IndexOutOfBoundsException(index)
        }
        channels[index] = value
    }

    /**
     * The state of a single channel.
     */
    enum class Value {
        NONE,
        FALSE,
        TRUE,
        INVALID,
        ;

        companion object {

            /**
             * Returns the resulting [Value] if [value1] and [value2] are merged.
             */
            fun merge(value1: Value, value2: Value): Value {
                // Return one state if the other is `NONE`.
                if (value1 == NONE) {
                    return value2
                }
                if (value2 == NONE) {
                    return value1
                }

                // Return `INVALID` if one of the two states is `INVALID`.
                if (value1 == Value.INVALID || value2 == Value.INVALID) {
                    return INVALID
                }

                // Return `INVALID´ if the two states are not the same. (NONE is already handled)
                if (value1 != value2) {
                    return INVALID
                }

                // Return the shared state.
                return value1
            }
        }
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

    companion object {

        val NONE = LogicState(0) { Value.NONE }

        fun value(value: Value, size: Int): LogicState {
            return LogicState(size) { value }
        }

        /**
         * Merge the two logic states [state1] and [state2].
         */
        fun merge(state1: LogicState, state2: LogicState): LogicState {
            val channelNumber = max(state1.channels.size, state2.channels.size)
            val channels: Array<Value> = Array(channelNumber) { channelIndex ->
                val value1 = state1.channelOrNone(channelIndex)
                val value2 = state2.channelOrNone(channelIndex)
                Value.merge(value1, value2)
            }
            return LogicState(channels)
        }
    }
}
