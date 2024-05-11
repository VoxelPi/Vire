package net.voxelpi.vire.engine.environment

import net.voxelpi.event.EventScope
import net.voxelpi.event.eventScope
import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.CircuitImpl

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
    public fun createCircuit(id: Identifier): Circuit
}

internal class EnvironmentImpl : Environment {

    override val eventScope: EventScope = eventScope()

    override fun createCircuit(id: Identifier): Circuit {
        return CircuitImpl(this, id)
    }
}
