package net.voxelpi.vire.api.simulation.event.component

import net.voxelpi.vire.api.simulation.component.Component

/**
 * An event that is called when a component is configured.
 */
data class ComponentConfigureEvent(
    override val component: Component,
) : ComponentEvent
