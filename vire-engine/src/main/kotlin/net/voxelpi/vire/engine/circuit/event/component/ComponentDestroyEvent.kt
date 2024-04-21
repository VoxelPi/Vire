package net.voxelpi.vire.engine.circuit.event.component

import net.voxelpi.vire.engine.circuit.component.Component

/**
 * An event that is called when a component is destroyed.
 */
public data class ComponentDestroyEvent(
    override val component: Component,
) : ComponentEvent
