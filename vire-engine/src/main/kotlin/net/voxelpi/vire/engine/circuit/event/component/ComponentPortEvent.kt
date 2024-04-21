package net.voxelpi.vire.engine.circuit.event.component

import net.voxelpi.vire.engine.circuit.component.Component
import net.voxelpi.vire.engine.circuit.component.ComponentPort

/**
 * An event that affect a component port.
 */
public interface ComponentPortEvent : ComponentEvent {

    /**
     * The affected component port.
     */
    public val port: ComponentPort

    override val component: Component
        get() = port.component
}
