package net.voxelpi.vire.api.simulation.event.simulation.component.port

import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.component.ComponentPort
import net.voxelpi.vire.api.simulation.event.simulation.component.ComponentEvent

interface ComponentPortEvent : ComponentEvent {

    val port: ComponentPort

    override val component: Component
        get() = port.component
}
