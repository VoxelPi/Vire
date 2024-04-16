package net.voxelpi.vire.engine.simulation

import io.github.oshai.kotlinlogging.KotlinLogging
import net.voxelpi.event.EventScope
import net.voxelpi.vire.api.environment.Environment
import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.engine.circuit.VireCircuit

class VireSimulation(
    override val environment: Environment,
    override val circuit: VireCircuit,
) : Simulation {

    private val logger = KotlinLogging.logger {}

    override val eventScope: EventScope = environment.eventScope.createSubScope()

    override fun simulateSteps(numberOfSteps: Int) {
        for (i in 1..numberOfSteps) {
            circuit.simulateStep()
        }
    }
}
