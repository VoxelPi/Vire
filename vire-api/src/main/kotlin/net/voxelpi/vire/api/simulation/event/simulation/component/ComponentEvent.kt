package net.voxelpi.vire.api.simulation.event.simulation.component

import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.event.SimulationEvent

/**
 * An event that affect a component.
 */
interface ComponentEvent : SimulationEvent {

    /**
     * The affected component.
     */
    val component: Component

    override val simulation: Simulation
        get() = component.simulation
}
