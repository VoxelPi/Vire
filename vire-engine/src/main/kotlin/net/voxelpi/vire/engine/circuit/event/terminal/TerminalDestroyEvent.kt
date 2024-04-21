package net.voxelpi.vire.engine.circuit.event.terminal

import net.voxelpi.vire.engine.circuit.terminal.Terminal

/**
 * An event that is called when a terminal is destroyed.
 */
public data class TerminalDestroyEvent(
    override val terminal: Terminal,
) : TerminalEvent
