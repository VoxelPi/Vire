package net.voxelpi.vire.api.simulation.network

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class NetworkStateTest {

    @Test
    fun notNone() {
        assertEquals(NetworkState.value(true, 1), !NetworkState.None)
    }

    @Test
    fun notValue() {
        val state = NetworkState.Value(booleanArrayOf(false, true))
        assertEquals(NetworkState.Value(booleanArrayOf(true, false)), !state)
    }

    @Test
    fun mergeNoneNone() {
        val stateA = NetworkState.None
        val stateB = NetworkState.None
        assertEquals(NetworkState.None, NetworkState.merge(stateA, stateB))
    }

    @Test
    fun mergeNoneValue() {
        val stateA = NetworkState.None
        val stateB = NetworkState.value(false, 4)
        assertEquals(stateB, NetworkState.merge(stateA, stateB))
        assertEquals(stateB, NetworkState.merge(stateB, stateA))
    }

    @Test
    fun mergeValue() {
        val stateA = NetworkState.value(false, 4)
        val stateB = NetworkState.value(false, 4)
        assertEquals(stateB, NetworkState.merge(stateA, stateB))
    }

    @Test
    fun mergeInvalid() {
        assertEquals(NetworkState.Invalid, NetworkState.merge(NetworkState.value(false, 4), NetworkState.Invalid))
        assertEquals(NetworkState.Invalid, NetworkState.merge(NetworkState.None, NetworkState.Invalid))
    }

    @Test
    fun mergeDifferentValues() {
        assertEquals(NetworkState.Invalid, NetworkState.merge(NetworkState.value(false, 4), NetworkState.value(true, 4)))
        assertEquals(NetworkState.Invalid, NetworkState.merge(NetworkState.value(false, 3), NetworkState.value(false, 4)))
    }
}
