package net.voxelpi.vire.engine.circuit.event.terminal

import net.voxelpi.vire.engine.circuit.terminal.Terminal

/**
 * An event that is called when a new terminal is created.
 */
public data class TerminalCreateEvent(
    override val terminal: Terminal,
) : TerminalEvent
