package net.voxelpi.vire.engine.circuit.event.terminal

import net.voxelpi.vire.engine.circuit.kernel.variable.IOVectorElement
import net.voxelpi.vire.engine.circuit.terminal.Terminal

/**
 * An event that is called when the variable of a terminal is selected.
 * @property newVariable the new variable.
 * @property oldVariable the old variable.
 */
public data class TerminalSelectVariableEvent(
    override val terminal: Terminal,
    val newVariable: IOVectorElement?,
    val oldVariable: IOVectorElement?,
) : TerminalEvent