package net.voxelpi.vire.api.simulation.library

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.component.StateMachine

/**
 * A library that adds content to the simulation.
 * @property id the id of the library.
 * @property name the name of the library.
 * @property description a description of the library.
 */
abstract class Library(
    val id: String,
    val name: String,
    val description: String,
) {
    /**
     * A map of all state machines.
     */
    protected val stateMachines: MutableMap<Identifier, StateMachine> = mutableMapOf()

    /**
     * Registers a new state machine in the library.
     */
    protected fun register(stateMachine: StateMachine) {
        require(!stateMachines.containsKey(stateMachine.key)) { "The module already has a state machine with the id ${stateMachine.key}." }

        stateMachines[stateMachine.key] = stateMachine
    }

    /**
     * Returns all state machines provided by the library.
     */
    fun stateMachines(): Collection<StateMachine> {
        return stateMachines.values
    }

    /**
     * Returns the state machine with the given [identifier]
     */
    fun stateMachine(identifier: Identifier): StateMachine? {
        return stateMachines[identifier]
    }
}
