package net.voxelpi.vire.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LogicStateTest {

    @Test
    fun booleanStateConversion() {
        val logicState = logicState(true, false, true, true)
        assertEquals(booleanState(true, false, true, true), logicState.booleanState())
        assertEquals(booleanState(true, false), logicState.booleanState(2))
        assertEquals(booleanState(true, false, true, true, false, false), logicState.booleanState(6))
    }

    @Test
    fun mergeNoneNone() {
        val stateA = LogicState.EMPTY
        val stateB = LogicState.EMPTY
        assertEquals(LogicState.EMPTY, LogicState.merge(stateA, stateB))
    }

    @Test
    fun mergeNoneValue() {
        val stateA = LogicState.EMPTY
        val stateB = LogicState.value(LogicValue.NONE, 4)
        assertEquals(stateB, LogicState.merge(stateA, stateB))
        assertEquals(stateB, LogicState.merge(stateB, stateA))
    }

    @Test
    fun mergeValue() {
        val stateA = LogicState.value(false, 4)
        val stateB = LogicState.value(false, 4)
        assertEquals(stateB, LogicState.merge(stateA, stateB))
    }

    @Test
    fun mergeDifferentValues() {
        assertEquals(LogicValue.INVALID, LogicState.merge(LogicState.value(false), LogicState.value(true))[0])
    }
}
