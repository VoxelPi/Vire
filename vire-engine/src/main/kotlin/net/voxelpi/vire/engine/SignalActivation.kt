package net.voxelpi.vire.engine

/**
 * The transition type of boolean signal.
 */
public enum class SignalActivation {

    /**
     * The signal is active, if the level is high (1).
     */
    ACTIVE_HIGH,

    /**
     * The signal is active, if the level is low (0).
     */
    ACTIVE_LOW,
    ;

    /**
     * The id of the signal activation.
     */
    public val id: String
        get() = name.lowercase()

    /**
     * Checks if the given [value] results in an active state.
     */
    public fun isActive(value: LogicState): Boolean {
        return when (this) {
            ACTIVE_HIGH -> value.toBoolean()
            ACTIVE_LOW -> !value.toBoolean()
        }
    }

    public companion object {

        /**
         * Returns the signal activation with the given [id].
         */
        public fun fromId(id: String): SignalActivation? = entries.find { it.id == id }
    }
}
