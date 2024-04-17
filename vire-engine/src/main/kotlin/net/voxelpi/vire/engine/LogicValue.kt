package net.voxelpi.vire.engine

/**
 * The state of a single channel.
 */
public enum class LogicValue {
    NONE,
    FALSE,
    TRUE,
    INVALID,
    ;

    public companion object {

        /**
         * Returns the resulting [LogicValue] if [value1] and [value2] are merged.
         */
        public fun merge(value1: LogicValue, value2: LogicValue): LogicValue {
            // Return one state if the other is `NONE`.
            if (value1 == NONE) {
                return value2
            }
            if (value2 == NONE) {
                return value1
            }

            // Return `INVALID` if one of the two states is `INVALID`.
            if (value1 == INVALID || value2 == INVALID) {
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

/**
 * Converts the boolean into the corresponding logic value.
 */
public fun Boolean?.logicValue(): LogicValue {
    return when (this) {
        null -> LogicValue.NONE
        true -> LogicValue.TRUE
        false -> LogicValue.FALSE
    }
}
