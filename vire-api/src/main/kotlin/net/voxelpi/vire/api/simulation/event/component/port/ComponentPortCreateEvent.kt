package net.voxelpi.vire.api.simulation.event.component.port

import net.voxelpi.vire.api.simulation.component.ComponentPort

/**
 * An event that is called when a component port is created.
 */
data class ComponentPortCreateEvent(
    override val port: ComponentPort,
) : ComponentPortEvent
