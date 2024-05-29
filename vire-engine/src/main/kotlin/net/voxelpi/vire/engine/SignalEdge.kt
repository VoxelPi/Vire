package net.voxelpi.vire.engine

/**
 * The transition type of boolean signal.
 */
public enum class SignalEdge {

    /**
     * Low-to-high transition.
     */
    RISING_EDGE,

    /**
     * High-to-low transition.
     */
    FALLING_EDGE,

    /**
     * Both edges.
     */
    RISING_AND_FALLING_EDGES,
    ;

    /**
     * The id of the signal edge.
     */
    public val id: String
        get() = name.lowercase()

    /**
     * Checks if the given signal change has the signal edge.
     */
    public fun isTriggered(state0: LogicState, state1: LogicState): Boolean {
        return isTriggered(state0.toBoolean(), state1.toBoolean())
    }

    /**
     * Checks if the given signal change has the signal edge.
     */
    public fun isTriggered(state0: Boolean, state1: Boolean): Boolean {
        return when (this) {
            RISING_EDGE -> !state0 && state1
            FALLING_EDGE -> state0 && !state1
            RISING_AND_FALLING_EDGES -> state0 != state1
        }
    }

    public companion object {

        /**
         * Returns the signal edge with the given [id].
         */
        public fun fromId(id: String): SignalEdge? = entries.find { it.id == id }
    }
}
