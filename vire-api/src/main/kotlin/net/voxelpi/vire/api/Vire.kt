package net.voxelpi.vire.api

import net.voxelpi.vire.api.circuit.library.Library
import net.voxelpi.vire.api.circuit.statemachine.StateMachineFactory
import net.voxelpi.vire.api.simulation.Simulation
import org.jetbrains.annotations.ApiStatus.Internal

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
     * The state machine factory.
     */
    val stateMachineFactory: StateMachineFactory

    /**
     * Creates a new simulation with the given [libraries].
     */
    fun createSimulation(libraries: List<Library>): Simulation

    companion object {

        @Internal
        private var instance: Vire? = null

        /**
         * Gets the singleton instance.
         */
        fun get(): Vire {
            return instance ?: throw IllegalStateException("No implementation of the Vire Simulation Engine is loaded")
        }

        @Internal
        fun register(instance: Vire) {
            this.instance = instance
        }

        @Internal
        fun unregister() {
            this.instance = null
        }
    }
}
