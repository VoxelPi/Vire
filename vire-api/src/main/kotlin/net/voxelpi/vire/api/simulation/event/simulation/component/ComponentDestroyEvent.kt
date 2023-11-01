package net.voxelpi.vire.api.simulation.event.simulation.component

import net.voxelpi.vire.api.simulation.component.Component

data class ComponentDestroyEvent(
    override val component: Component,
) : ComponentEvent
