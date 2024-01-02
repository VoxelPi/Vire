package net.voxelpi.vire.api.simulation

/**
 * The state of a single channel.
 */
enum class LogicValue {
    NONE,
    FALSE,
    TRUE,
    INVALID,
    ;

    companion object {

        /**
         * Returns the resulting [LogicValue] if [value1] and [value2] are merged.
         */
        fun merge(value1: LogicValue, value2: LogicValue): LogicValue {
            // Return one state if the other is `NONE`.
            if (value1 == NONE) {
                return value2
            }
            if (value2 == NONE) {
                return value1
            }

            // Return `INVALID` if one of the two states is `INVALID`.
            if (value1 == LogicValue.INVALID || value2 == LogicValue.INVALID) {
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

fun Boolean?.logicValue(): LogicValue {
    return when (this) {
        null -> LogicValue.NONE
        true -> LogicValue.TRUE
        false -> LogicValue.FALSE
    }
}
