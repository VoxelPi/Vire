package net.voxelpi.vire.engine.simulation

import net.voxelpi.event.EventScope
import net.voxelpi.event.eventScope
import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.api.simulation.library.Library
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import org.slf4j.LoggerFactory

class VireSimulation(
    libraries: List<Library>,
) : Simulation {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override val eventScope: EventScope = eventScope()

    override val circuit: VireCircuit

    private val libraries: Map<String, Library>
    private val stateMachines: Map<Identifier, StateMachine>

    init {
        // Register libraries
        this.libraries = libraries.associateBy { it.id }
        logger.info("Loaded ${libraries.size} libraries: ${libraries.joinToString(", ", "[", "]") { it.name }}")

        // Register state machines
        val stateMachines = mutableMapOf<Identifier, StateMachine>()
        this.libraries.values.forEach { stateMachines.putAll(it.stateMachines().associateBy(StateMachine::id)) }
        this.stateMachines = stateMachines
        logger.info("Registered ${stateMachines.size} state machines")

        // Create the simulated circuit.
        circuit = VireCircuit(this)
        logger.info("Created circuit")
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

    override fun stateMachines(): List<StateMachine> {
        return stateMachines.values.toList()
    }

    override fun simulateSteps(numberOfSteps: Int) {
        for (i in 1..numberOfSteps) {
            circuit.simulateStep()
        }
    }
}
