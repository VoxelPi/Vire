package net.voxelpi.vire.engine.circuit.event.component

import net.voxelpi.vire.engine.circuit.component.ComponentPort

/**
 * An event that is called when a new component port is created.
 */
public data class ComponentPortCreateEvent(
    override val port: ComponentPort,
) : ComponentPortEvent
