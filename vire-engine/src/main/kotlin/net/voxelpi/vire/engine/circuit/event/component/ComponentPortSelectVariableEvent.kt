package net.voxelpi.vire.engine.circuit.event.component

import net.voxelpi.vire.engine.circuit.component.ComponentPort
import net.voxelpi.vire.engine.kernel.variable.IOVectorElement

/**
 * An event that is called when the variable of a component port is selected.
 * @property newVariable the new variable.
 * @property oldVariable the old variable.
 */
public data class ComponentPortSelectVariableEvent(
    override val port: ComponentPort,
    val newVariable: IOVectorElement?,
    val oldVariable: IOVectorElement?,
) : ComponentPortEvent
