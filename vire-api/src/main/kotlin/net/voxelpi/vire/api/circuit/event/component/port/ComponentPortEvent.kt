package net.voxelpi.vire.api.circuit.event.component.port

import net.voxelpi.vire.api.circuit.component.Component
import net.voxelpi.vire.api.circuit.component.ComponentPort
import net.voxelpi.vire.api.circuit.event.component.ComponentEvent

/**
 * An event that affect a component port.
 */
interface ComponentPortEvent : ComponentEvent {

    /**
     * The affected component port.
     */
    val port: ComponentPort

    override val component: Component
        get() = port.component
}
