package net.voxelpi.vire.engine.circuit.event.component

import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.component.Component
import net.voxelpi.vire.engine.circuit.event.CircuitEvent

/**
 * An event that affect a component.
 */
public interface ComponentEvent : CircuitEvent {

    /**
     * The affected component.
     */
    public val component: Component

    override val circuit: Circuit
        get() = component.circuit
}
