package net.voxelpi.vire.api.circuit.event.component

import net.voxelpi.vire.api.circuit.Circuit
import net.voxelpi.vire.api.circuit.component.Component
import net.voxelpi.vire.api.circuit.event.CircuitEvent

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
