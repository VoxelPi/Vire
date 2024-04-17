package net.voxelpi.vire.engine.environment

import net.voxelpi.event.EventScope
import net.voxelpi.event.eventScope
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.MutableCircuitImpl

/**
 * A vire environment.
 */
public interface Environment {

    /**
     * The event scope of the environment.
     */
    public val eventScope: EventScope

    /**
     * Creates a new circuit.
     */
    public fun createCircuit(): Circuit
}

internal class EnvironmentImpl : Environment {

    override val eventScope: EventScope = eventScope()

    override fun createCircuit(): Circuit {
        return MutableCircuitImpl(this)
    }
}
