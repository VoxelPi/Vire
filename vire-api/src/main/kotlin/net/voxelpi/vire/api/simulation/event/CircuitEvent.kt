package net.voxelpi.vire.api.simulation.event

import net.voxelpi.vire.api.simulation.Circuit
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
