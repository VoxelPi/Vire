package net.voxelpi.vire.api.circuit.event.component

import net.voxelpi.vire.api.circuit.component.Component

/**
 * An event that is called when a component is created.
 */
data class ComponentCreateEvent(
    override val component: Component,
) : ComponentEvent
