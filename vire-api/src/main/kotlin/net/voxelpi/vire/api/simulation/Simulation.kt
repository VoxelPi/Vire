package net.voxelpi.vire.api.simulation

import net.voxelpi.event.EventScope
import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.circuit.Circuit
import net.voxelpi.vire.api.circuit.library.Library
import net.voxelpi.vire.api.circuit.statemachine.StateMachine

/**
 * The simulation that manages the state of all components and networks.
 */
interface Simulation {

    /**
     * The event scope of the simulation.
     */
    val eventScope: EventScope

    /**
     * The circuit of the simulation.
     */
    val circuit: Circuit

    /**
     * Returns all registered libraries.
     */
    fun libraries(): Collection<Library>

    /**
     * Returns the [Library] with the given [id].
     */
    fun library(id: String): Library?

    /**
     * Returns a collection of all registered state machines.
     */
    fun stateMachines(): Collection<StateMachine>

    /**
     * Returns the state machine with the specified [identifier].
     * If no state machine with such key exists, `null` is returned.
     */
    fun stateMachine(identifier: Identifier): StateMachine?

    /**
     * Simulates [numberOfSteps] steps.
     */
    fun simulateSteps(numberOfSteps: Int)
}
