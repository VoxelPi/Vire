package net.voxelpi.vire.engine.circuit.event.component

import net.voxelpi.vire.engine.circuit.component.Component

/**
 * An event that is called when a new component is created.
 */
public data class ComponentCreateEvent(
    override val component: Component,
) : ComponentEvent
