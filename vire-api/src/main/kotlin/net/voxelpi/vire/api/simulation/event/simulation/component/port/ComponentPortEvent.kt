package net.voxelpi.vire.api.simulation.event.simulation.component.port

import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.component.ComponentPort
import net.voxelpi.vire.api.simulation.event.simulation.component.ComponentEvent

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
