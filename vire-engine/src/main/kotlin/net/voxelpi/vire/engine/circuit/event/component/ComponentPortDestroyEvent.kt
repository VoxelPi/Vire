package net.voxelpi.vire.engine.circuit.event.component

import net.voxelpi.vire.engine.circuit.component.ComponentPort

/**
 * An event that is called when a component port is destroyed.
 */
public data class ComponentPortDestroyEvent(
    override val port: ComponentPort,
) : ComponentPortEvent
