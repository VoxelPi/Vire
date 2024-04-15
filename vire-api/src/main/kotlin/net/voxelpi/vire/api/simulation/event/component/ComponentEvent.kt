package net.voxelpi.vire.api.simulation.event.component

import net.voxelpi.vire.api.simulation.Circuit
import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.event.CircuitEvent

/**
 * An event that affect a component.
 */
interface ComponentEvent : CircuitEvent {

    /**
     * The affected component.
     */
    val component: Component

    override val circuit: Circuit
        get() = component.circuit
}