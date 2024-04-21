package net.voxelpi.vire.engine.circuit.event.terminal

import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.event.CircuitEvent
import net.voxelpi.vire.engine.circuit.terminal.Terminal

/**
 * An event that affect a component port.
 */
public interface TerminalEvent : CircuitEvent {

    /**
     * The affected terminal.
     */
    public val terminal: Terminal

    override val circuit: Circuit
        get() = terminal.circuit
}
