package net.voxelpi.vire.api.simulation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class BooleanStateTest {

    @Test
    fun logicStateConversion() {
        val booleanState = booleanState(true, false, true, true)
        assertEquals(logicState(true, false, true, true), booleanState.logicState())
        assertEquals(logicState(true, false), booleanState.logicState(2))
        assertEquals(logicState(true, false, true, true, null, null), booleanState.logicState(6))
    }

    @Test
    fun notNone() {
        assertEquals(BooleanState.value(true, 5), !BooleanState.value(false, 5))
        assertNotEquals(BooleanState.value(true, 5), !BooleanState.value(false, 4))
    }

    @Test
    fun notValue() {
        val state = BooleanState(booleanArrayOf(false, true))
        assertEquals(BooleanState(booleanArrayOf(true, false)), !state)
    }

    @Test
    fun toInt() {
        val state = booleanState(false, false, true, false, false, true, false, true)
        assertEquals(164, state.toInt())
    }

    @Test
    fun fromInt() {
        val state = booleanState(false, false, true, false, false, true, false, true)
        assertEquals(state, BooleanState.integer(164.toByte()))
        assertEquals(state, BooleanState.integer(164, 8))
    }
}
