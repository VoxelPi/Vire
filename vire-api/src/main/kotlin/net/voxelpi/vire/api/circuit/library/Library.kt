package net.voxelpi.vire.api.circuit.library

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.StateMachineProvider

/**
 * A library that adds content to the simulation.
 * @property id the id of the library.
 * @property name the name of the library.
 * @property description a description of the library.
 * @property dependencies a list of all the dependencies of the library.
 */
abstract class Library(
    val id: String,
    val name: String,
    val description: String,
    val dependencies: List<String>,
) {
    /**
     * A map of all state machines.
     */
    protected val stateMachines: MutableMap<Identifier, StateMachine> = mutableMapOf()

    /**
     * Registers the given [stateMachine] in the library.
     */
    protected fun register(stateMachine: StateMachine) {
        require(!stateMachines.containsKey(stateMachine.id)) {
            "The module already has a state machine with the id ${stateMachine.id}."
        }

        stateMachines[stateMachine.id] = stateMachine
    }

    /**
     * Registers the state machine provided by the given [stateMachineProvider] in the library.
     */
    protected fun register(stateMachineProvider: StateMachineProvider) {
        register(stateMachineProvider.stateMachine)
    }

    /**
     * Returns all state machines provided by the library.
     */
    fun stateMachines(): List<StateMachine> {
        return stateMachines.values.toList()
    }

    /**
     * Returns the state machine with the given [identifier]
     */
    fun stateMachine(identifier: Identifier): StateMachine? {
        return stateMachines[identifier]
    }
}
