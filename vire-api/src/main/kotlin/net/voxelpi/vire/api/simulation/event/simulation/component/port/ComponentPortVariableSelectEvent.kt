package net.voxelpi.vire.api.simulation.event.simulation.component.port

import net.voxelpi.vire.api.simulation.component.ComponentPort
import net.voxelpi.vire.api.simulation.component.ComponentPortVariableView

/**
 * An event that is called when the variable of port is selected.
 * @property newVariable the new variable.
 * @property oldVariable the old variable.
 */
class ComponentPortVariableSelectEvent(
    override val port: ComponentPort,
    val newVariable: ComponentPortVariableView?,
    val oldVariable: ComponentPortVariableView?,
) : ComponentPortEvent
