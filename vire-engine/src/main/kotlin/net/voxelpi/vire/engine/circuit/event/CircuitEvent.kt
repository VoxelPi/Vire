package net.voxelpi.vire.engine.circuit.event

import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.environment.event.EnvironmentEvent

/**
 * An event in a circuit.
 */
public interface CircuitEvent : EnvironmentEvent {

    /**
     * The affected circuit.
     */
    public val circuit: Circuit

    override val environment: Environment
        get() = circuit.environment
}
