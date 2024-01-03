package net.voxelpi.vire.api

import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.api.simulation.library.Library
import net.voxelpi.vire.api.simulation.statemachine.StateMachineFactory
import net.voxelpi.vire.api.util.ServiceProvider

interface Vire {

    /**
     * The name of the vire engine implementation.
     */
    val brand: String

    /**
     * The version of the vire engine.
     */
    val version: String

    /**
     * The exact version of the vire engine.
     */
    val longVersion: String

    /**
     * Creates a new simulation with the given [libraries].
     */
    fun createSimulation(libraries: List<Library>): Simulation

    companion object {

        /**
         * The state machine factory.
         */
        val stateMachineFactory = ServiceProvider<StateMachineFactory>()
    }
}
