package net.voxelpi.vire.api.simulation.event.simulation.component

import net.voxelpi.vire.api.simulation.component.Component

/**
 * An event that is called when a component is created.
 */
data class ComponentCreateEvent(
    override val component: Component,
) : ComponentEvent
