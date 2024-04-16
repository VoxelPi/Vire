package net.voxelpi.vire.api.environment

import net.voxelpi.event.EventScope
import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.circuit.Circuit
import net.voxelpi.vire.api.circuit.library.Library
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.Simulation

/**
 * A vire environment.
 */
interface Environment {

    /**
     * The event scope of the environment.
     */
    val eventScope: EventScope

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
     * Creates a new circuit.
     */
    fun createCircuit(): Circuit

    /**
     * Creates a new simulation for the given [circuit].
     */
    fun createSimulation(circuit: Circuit): Simulation
}
