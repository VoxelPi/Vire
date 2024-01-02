package net.voxelpi.vire.api

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

    companion object {

        /**
         * The state machine factory.
         */
        val stateMachineFactory = ServiceProvider<StateMachineFactory>()
    }
}
