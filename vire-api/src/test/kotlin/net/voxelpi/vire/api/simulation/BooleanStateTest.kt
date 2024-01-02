package net.voxelpi.vire.api.simulation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class BooleanStateTest {

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
}
