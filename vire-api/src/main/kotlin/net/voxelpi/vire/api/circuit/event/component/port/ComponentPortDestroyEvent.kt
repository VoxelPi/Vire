package net.voxelpi.vire.api.circuit.event.component.port

import net.voxelpi.vire.api.circuit.component.ComponentPort

/**
 * An event that is called when a component port is destroyed.
 */
data class ComponentPortDestroyEvent(
    override val port: ComponentPort,
) : ComponentPortEvent
