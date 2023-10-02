package net.voxelpi.vire.api.simulation.network

import kotlin.math.min

sealed interface NetworkState {

    /**
     * The state of a network that has not yet been assigned a state.
     */
    data object None : NetworkState

    /**
     * The state of a network that has been assigned multiple states.
     */
    data object Invalid : NetworkState

    /**
     * The state of a network set by a component.
     */
    data class Value(
        val value: BooleanArray,
    ) : NetworkState {
        val channels: Int
            get() = value.size

        /**
         * Creates an unsigned 64-bit integer using the first 64 values of the state as bits.
         */
        fun toULong(): ULong {
            var result = 0UL
            for (i in 0..<min(64, channels)) {
                if (value[i]) {
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
            for (i in 0..<min(32, channels)) {
                if (value[i]) {
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
            for (i in 0..<min(16, channels)) {
                if (value[i]) {
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
            for (i in 0..<min(16, channels)) {
                if (value[i]) {
                    result = result or 1U shl i
                }
            }
            return result.toUByte()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Value

            return value.contentEquals(other.value)
        }

        override fun hashCode(): Int {
            return value.contentHashCode()
        }
    }

    operator fun not(): NetworkState {
        return when {
            this == None -> value(true, 1)
            this is Value -> Value(BooleanArray(this.channels) { index -> !this.value[index]})
            else -> Invalid
        }
    }

    infix fun or(other: NetworkState): NetworkState {
        return bitwiseBiFunction(this, other) { a, b ->
            (a ?: false) || (b ?: false)
        }
    }

    infix fun and(other: NetworkState): NetworkState {
        return bitwiseBiFunction(this, other) { a, b ->
            (a ?: false) && (b ?: false)
        }
    }

    infix fun xor(other: NetworkState): NetworkState {
        return bitwiseBiFunction(this, other) { a, b ->
            (a ?: false) xor (b ?: false)
        }
    }

    infix fun nor(other: NetworkState): NetworkState {
        return or(other)
    }

    infix fun nand(other: NetworkState): NetworkState {
        return and(other)
    }

    infix fun xnor(other: NetworkState): NetworkState {
        return !xor(other)
    }

    companion object {

        fun value(value: Boolean, channels: Int = 1): NetworkState {
            return Value(BooleanArray(channels) { value })
        }

        /**
         * Combines two network states.
         */
        fun merge(state1: NetworkState, state2: NetworkState): NetworkState {
            // Check if one of the two states is unset.
            // If this is the case, return the other state.
            if (state1 == None) {
                return state2
            }
            if (state2 == None) {
                return state1
            }

            // Check if one of the two states is invalid.
            // If this is the case, return the invalid state.
            if (state1 == Invalid || state2 == Invalid) {
                return Invalid
            }

            // Check if both states are the same.
            // If this is the case, return the shared state.
            if (state1 == state2) {
                return state1
            }

            // The states are both set but not the same, therefore return the invalid state.
            return Invalid
        }

        fun bitwiseBiFunction(state1: NetworkState, state2: NetworkState, function: (Boolean?, Boolean?) -> Boolean): NetworkState {
            // Check if one of the two states is invalid.
            // If this is the case, return the invalid state.
            if (state1 == Invalid || state2 == Invalid) {
                return Invalid
            }

            // Check the number of channels.
            val channels = when {
                state1 == None && state2 == None -> return None
                state1 == None -> (state2 as Value).channels
                state2 == None -> (state1 as Value).channels
                (state1 as Value).channels != (state2 as Value).channels -> return Invalid
                else -> state1.channels
            }

            // Construct the new state.
            val value1 = (state1 as? Value)?.value
            val value2 = (state2 as? Value)?.value
            return Value(
                BooleanArray(channels) { index ->
                    function(value1?.get(index), value2?.get(index))
                }
            )
        }

        fun bitwiseNFunction(states: Array<NetworkState>, function: (Array<Boolean?>) -> Boolean): NetworkState {
            // Check if any of the states is invalid
            if (states.any { it == Invalid }) {
                return Invalid
            }

            val valueStates = states.filterIsInstance<Value>()
            if (valueStates.isEmpty()) {
                return None
            }

            val channels = valueStates.first().channels
            if (valueStates.any { it.channels != channels}) {
                return Invalid
            }

            return Value(
                BooleanArray(channels) { channel ->
                    function(Array(states.size) { index -> states[index].let { if (it is Value) it.value[channel] else null}})
                }
            )
        }

        fun or(states: Array<NetworkState>): NetworkState {
            return bitwiseNFunction(states) { values -> values.any { it ?: false } }
        }

        fun and(states: Array<NetworkState>): NetworkState {
            return bitwiseNFunction(states) { values -> values.isNotEmpty() && values.all { it ?: false } }
        }

        fun xor(states: Array<NetworkState>): NetworkState {
            return bitwiseNFunction(states) { values -> values.count { it ?: false } % 2 == 1 }
        }

        fun nor(states: Array<NetworkState>): NetworkState {
            return !or(states)
        }

        fun nand(states: Array<NetworkState>): NetworkState {
            return !nand(states)
        }

        fun xnor(states: Array<NetworkState>): NetworkState {
            return !xor(states)
        }
    }
}
