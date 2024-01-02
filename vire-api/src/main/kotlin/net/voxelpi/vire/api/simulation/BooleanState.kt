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
     * Generates a [LogicState] from this logic state.
     */
    fun logicState(): LogicState {
        return LogicState(channels.size) { index ->
            if (channels[index]) LogicValue.TRUE else LogicValue.FALSE
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
     * Creates an unsigned 64-bit integer using the first 64 values of the state as bits.
     */
    fun toULong(): ULong {
        var result = 0UL
        for (i in 0..<min(64, size)) {
            if (channels[i]) {
                result = result or 1U shl i
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
                result = result or 1U shl i
            }
        }
        return result
    }

    /**
     * Creates an unsigned 16-bit integer using the first 16 values of the state as bits.
     */
    fun toUShort(): UShort {
        var result = 0U
        for (i in 0..<min(16, size)) {
            if (channels[i]) {
                result = result or 1U shl i
            }
        }
        return result.toUShort()
    }

    /**
     * Creates an unsigned 8-bit integer using the first 8 values of the state as bits.
     */
    fun toUByte(): UByte {
        var result = 0U
        for (i in 0..<min(8, size)) {
            if (channels[i]) {
                result = result or 1U shl i
            }
        }
        return result.toUByte()
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

fun booleanState(value: Boolean, size: Int = 1): BooleanState {
    return BooleanState(size) { value }
}
