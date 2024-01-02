package net.voxelpi.vire.api.simulation.event.simulation.statemachine

import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.api.simulation.event.SimulationEvent
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInstance

/**
 * An event that is called when a component is configured.
 */
data class StateMachineConfigureEvent(
    override val simulation: Simulation,
    val stateMachineInstance: StateMachineInstance,
) : SimulationEvent
