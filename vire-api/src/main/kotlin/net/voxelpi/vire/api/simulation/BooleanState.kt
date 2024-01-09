package net.voxelpi.vire.api.simulation

import kotlin.math.min

data class BooleanState(val channels: BooleanArray) {

    constructor(size: Int, init: (Int) -> Boolean) : this(BooleanArray(size, init))

    /**
     * The number of channels of the state.
     */
    val size: Int
        get() = channels.size

    /**
     * Creates a resized copy with the given [size].
     * If [size] is greater than the current size, the remaining entries are filled with false.
     */
    fun resizedCopy(size: Int): BooleanState {
        return BooleanState(size) { index ->
            if (index < channels.size) channels[index] else false
        }
    }

    /**
     * Returns true if the state has at least one channel that is TRUE.
     */
    fun toBoolean(): Boolean {
        return channels.isNotEmpty() && channels.any { it }
    }

    /**
     * Generates a [LogicState] from this logic state.
     */
    fun logicState(): LogicState {
        return LogicState(channels.size) { index ->
            if (channels[index]) LogicValue.TRUE else LogicValue.FALSE
        }
    }

    /**
     * Generates a [LogicState] from this logic state with the given [size].
     * If [size] is greater than the size of this state, the remaining channels are filled with [fillValue].
     */
    fun logicState(size: Int, fillValue: LogicValue = LogicValue.NONE): LogicState {
        return LogicState(size) { index ->
            when {
                index >= channels.size -> fillValue
                channels[index] -> LogicValue.TRUE
                else -> LogicValue.FALSE
            }
        }
    }

    operator fun get(index: Int): Boolean {
        return channels[index]
    }

    operator fun set(index: Int, value: Boolean) {
        channels[index] = value
    }

    operator fun not(): BooleanState {
        return BooleanState(BooleanArray(size) { index -> !channels[index] })
    }

    infix fun or(other: BooleanState): BooleanState {
        return bitwiseBiFunction(this, other) { a, b ->
            a || b
        }
    }

    infix fun and(other: BooleanState): BooleanState {
        return bitwiseBiFunction(this, other) { a, b ->
            a && b
        }
    }

    infix fun xor(other: BooleanState): BooleanState {
        return bitwiseBiFunction(this, other) { a, b ->
            a xor b
        }
    }

    infix fun nor(other: BooleanState): BooleanState {
        return !or(other)
    }

    infix fun nand(other: BooleanState): BooleanState {
        return !and(other)
    }

    infix fun xnor(other: BooleanState): BooleanState {
        return !xor(other)
    }

    /**
     * Creates an unsigned 8-bit integer using the first 8 values of the state as bits.
     */
    fun toByte(): Byte {
        var result = 0
        for (i in 0..<min(8, size)) {
            if (channels[i]) {
                result = result or (1 shl i)
            }
        }
        return result.toByte()
    }

    /**
     * Creates an unsigned 8-bit integer using the first 8 values of the state as bits.
     */
    fun toUByte(): UByte {
        var result = 0U
        for (i in 0..<min(8, size)) {
            if (channels[i]) {
                result = result or (1U shl i)
            }
        }
        return result.toUByte()
    }

    /**
     * Creates an unsigned 16-bit integer using the first 16 values of the state as bits.
     */
    fun toShort(): Short {
        var result = 0
        for (i in 0..<min(16, size)) {
            if (channels[i]) {
                result = result or (1 shl i)
            }
        }
        return result.toShort()
    }

    /**
     * Creates an unsigned 16-bit integer using the first 16 values of the state as bits.
     */
    fun toUShort(): UShort {
        var result = 0U
        for (i in 0..<min(16, size)) {
            if (channels[i]) {
                result = result or (1U shl i)
            }
        }
        return result.toUShort()
    }

    /**
     * Creates an unsigned 32-bit integer using the first 32 values of the state as bits.
     */
    fun toInt(): Int {
        var result = 0
        for (i in 0..<min(32, size)) {
            if (channels[i]) {
                result = result or (1 shl i)
            }
        }
        return result
    }

    /**
     * Creates an unsigned 32-bit integer using the first 32 values of the state as bits.
     */
    fun toUInt(): UInt {
        var result = 0U
        for (i in 0..<min(32, size)) {
            if (channels[i]) {
                result = result or (1U shl i)
            }
        }
        return result
    }

    /**
     * Creates an unsigned 64-bit integer using the first 64 values of the state as bits.
     */
    fun toLong(): Long {
        var result = 0L
        for (i in 0..<min(64, size)) {
            if (channels[i]) {
                result = result or (1L shl i)
            }
        }
        return result
    }

    /**
     * Creates an unsigned 64-bit integer using the first 64 values of the state as bits.
     */
    fun toULong(): ULong {
        var result = 0UL
        for (i in 0..<min(64, size)) {
            if (channels[i]) {
                result = result or (1UL shl i)
            }
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BooleanState

        return channels.contentEquals(other.channels)
    }

    override fun hashCode(): Int {
        return channels.contentHashCode()
    }

    companion object {

        fun value(value: Boolean, size: Int = 1): BooleanState {
            return BooleanState(size) { value }
        }

        /**
         * Returns a boolean state with the bits of the given number.
         */
        fun integer(value: Byte, size: Int = 8): BooleanState {
            require(size in 0..8) { "Invalid size." }
            return BooleanState(size) { index ->
                (value.toInt() shr index) and 1 == 1
            }
        }

        /**
         * Returns a boolean state with the bits of the given number.
         */
        fun integer(value: UByte, size: Int = 8): BooleanState {
            require(size in 0..8) { "Invalid size." }
            return BooleanState(size) { index ->
                (value.toUInt() shr index) and 1U == 1U
            }
        }

        /**
         * Returns a boolean state with the bits of the given number.
         */
        fun integer(value: Short, size: Int = 16): BooleanState {
            require(size in 0..16) { "Invalid size." }
            return BooleanState(size) { index ->
                (value.toInt() shr index) and 1 == 1
            }
        }

        /**
         * Returns a boolean state with the bits of the given number.
         */
        fun integer(value: UShort, size: Int = 16): BooleanState {
            require(size in 0..16) { "Invalid size." }
            return BooleanState(size) { index ->
                (value.toUInt() shr index) and 1U == 1U
            }
        }

        /**
         * Returns a boolean state with the bits of the given number.
         */
        fun integer(value: Int, size: Int = 32): BooleanState {
            require(size in 0..32) { "Invalid size." }
            return BooleanState(size) { index ->
                (value shr index) and 1 == 1
            }
        }

        /**
         * Returns a boolean state with the bits of the given number.
         */
        fun integer(value: UInt, size: Int = 32): BooleanState {
            require(size in 0..32) { "Invalid size." }
            return BooleanState(size) { index ->
                (value shr index) and 1U == 1U
            }
        }

        /**
         * Returns a boolean state with the bits of the given number.
         */
        fun integer(value: Long, size: Int = 64): BooleanState {
            require(size in 0..64) { "Invalid size." }
            return BooleanState(size) { index ->
                (value shr index) and 1L == 1L
            }
        }

        /**
         * Returns a boolean state with the bits of the given number.
         */
        fun integer(value: ULong, size: Int = 64): BooleanState {
            require(size in 0..64) { "Invalid size." }
            return BooleanState(size) { index ->
                (value shr index) and 1UL == 1UL
            }
        }

        /**
         * Calculates a new boolean state using a bitwise operation with [state1] and [state2].
         */
        fun bitwiseBiFunction(state1: BooleanState, state2: BooleanState, function: (Boolean, Boolean) -> Boolean): BooleanState {
            // Calculate the number of channels.
            require(state1.size == state2.size) { "States are of different size" }
            val size = state1.size

            // Construct the new state.
            return BooleanState(size) { index ->
                function(state1[index], state2[index])
            }
        }

        fun bitwiseNFunction(states: Array<BooleanState>, function: (Array<Boolean>) -> Boolean): BooleanState {
            // Calculate the number of channels.
            val size = states.first().size
            require(!states.any { it.size != size })

            // Construct the new state.
            return BooleanState(size) { channel ->
                function(Array(states.size) { index -> states[index][channel] })
            }
        }

        fun bitwiseNFunction(states: List<BooleanState>, function: (Array<Boolean>) -> Boolean): BooleanState {
            // Calculate the number of channels.
            val size = states.first().size
            require(!states.any { it.size != size })

            // Construct the new state.
            return BooleanState(size) { channel ->
                function(Array(states.size) { index -> states[index][channel] })
            }
        }

        fun or(states: Array<BooleanState>): BooleanState {
            return bitwiseNFunction(states) { values -> values.any { it } }
        }

        fun or(states: List<BooleanState>): BooleanState {
            return bitwiseNFunction(states) { values -> values.any { it } }
        }

        fun and(states: Array<BooleanState>): BooleanState {
            return bitwiseNFunction(states) { values -> values.isNotEmpty() && values.all { it } }
        }

        fun and(states: List<BooleanState>): BooleanState {
            return bitwiseNFunction(states) { values -> values.isNotEmpty() && values.all { it } }
        }

        fun xor(states: Array<BooleanState>): BooleanState {
            return bitwiseNFunction(states) { values -> values.count { it } % 2 == 1 }
        }

        fun xor(states: List<BooleanState>): BooleanState {
            return bitwiseNFunction(states) { values -> values.count { it } % 2 == 1 }
        }

        fun nor(states: Array<BooleanState>): BooleanState {
            return !or(states)
        }

        fun nor(states: List<BooleanState>): BooleanState {
            return !or(states)
        }

        fun nand(states: Array<BooleanState>): BooleanState {
            return !nand(states)
        }

        fun nand(states: List<BooleanState>): BooleanState {
            return !nand(states)
        }

        fun xnor(states: Array<BooleanState>): BooleanState {
            return !xor(states)
        }

        fun xnor(states: List<BooleanState>): BooleanState {
            return !xor(states)
        }
    }
}

fun Array<BooleanState>.logicStates(): Array<LogicState> {
    return Array(size) { index ->
        this[index].logicState()
    }
}

/**
 * Generates a new boolean state with the given [size], where each channel is set to [value].
 */
fun booleanState(value: Boolean, size: Int): BooleanState {
    return BooleanState(size) { value }
}

/**
 * Generates a new boolean state with the given [values].
 */
fun booleanState(vararg values: Boolean): BooleanState {
    return BooleanState(values)
}
