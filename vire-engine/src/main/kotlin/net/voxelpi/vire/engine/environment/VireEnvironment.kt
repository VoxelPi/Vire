package net.voxelpi.vire.engine.environment

import io.github.oshai.kotlinlogging.KotlinLogging
import net.voxelpi.event.EventScope
import net.voxelpi.event.eventScope
import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.circuit.Circuit
import net.voxelpi.vire.api.circuit.library.Library
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.circuit.Input
import net.voxelpi.vire.api.circuit.statemachine.circuit.Output
import net.voxelpi.vire.api.environment.Environment
import net.voxelpi.vire.engine.circuit.VireCircuit
import net.voxelpi.vire.engine.circuit.statemachine.VireStateMachine
import net.voxelpi.vire.engine.simulation.VireSimulation

class VireEnvironment(
    libraries: List<Library>,
) : Environment {

    private val logger = KotlinLogging.logger {}

    override val eventScope: EventScope = eventScope()

    private val libraries: Map<String, Library>
    private val stateMachines: Map<Identifier, VireStateMachine>

    init {
        // Register libraries
        this.libraries = libraries.associateBy { it.id }
        logger.info { "Loaded ${libraries.size} libraries: ${libraries.joinToString(", ", "[", "]") { it.name }}" }

        // Register internal state machines.
        val stateMachines = mutableMapOf<Identifier, VireStateMachine>(
            Input.stateMachine.id to Input.stateMachine as VireStateMachine,
            Output.stateMachine.id to Output.stateMachine as VireStateMachine,
        )

        // Register library state machines.
        for (library in libraries) {
            stateMachines += library.stateMachines()
                .map { it as VireStateMachine }
                .associateBy(StateMachine::id)
        }

        this.stateMachines = stateMachines
        logger.info { "Registered ${stateMachines.size} state machines" }
    }

    override fun libraries(): List<Library> {
        return libraries.values.toList()
    }

    override fun library(id: String): Library? {
        return libraries[id]
    }

    override fun stateMachine(identifier: Identifier): StateMachine? {
        return stateMachines[identifier]
    }

    override fun stateMachines(): List<VireStateMachine> {
        return stateMachines.values.toList()
    }

    override fun createCircuit(): VireCircuit {
        return VireCircuit(this)
    }

    override fun createSimulation(circuit: Circuit): VireSimulation {
        return VireSimulation(this, circuit as VireCircuit)
    }
}
