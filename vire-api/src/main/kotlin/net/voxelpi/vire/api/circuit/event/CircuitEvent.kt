package net.voxelpi.vire.api.circuit.event

import net.voxelpi.vire.api.circuit.Circuit
import net.voxelpi.vire.api.environment.Environment
import net.voxelpi.vire.api.environment.event.EnvironmentEvent

/**
 * An event in a circuit.
 */
interface CircuitEvent : EnvironmentEvent {

    /**
     * The affected circuit.
     */
    val circuit: Circuit

    override val environment: Environment
        get() = circuit.environment
}
