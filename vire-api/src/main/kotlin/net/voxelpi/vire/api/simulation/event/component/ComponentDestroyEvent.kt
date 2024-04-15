package net.voxelpi.vire.api.simulation.event.component

import net.voxelpi.vire.api.simulation.component.Component

/**
 * An event that is called when a component is destroyed.
 */
data class ComponentDestroyEvent(
    override val component: Component,
) : ComponentEvent
