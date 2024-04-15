package net.voxelpi.vire.api.circuit.event

import net.voxelpi.vire.api.circuit.Circuit
import net.voxelpi.vire.api.simulation.Simulation

/**
 * An event in a circuit.
 */
interface CircuitEvent : SimulationEvent {

    /**
     * The affected circuit.
     */
    val circuit: Circuit

    override val simulation: Simulation
        get() = circuit.simulation
}
