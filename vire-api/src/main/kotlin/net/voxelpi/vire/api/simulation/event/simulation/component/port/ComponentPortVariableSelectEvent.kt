package net.voxelpi.vire.api.simulation.event.simulation.component.port

import net.voxelpi.vire.api.simulation.component.ComponentPort
import net.voxelpi.vire.api.simulation.component.ComponentPortVariableView

class ComponentPortVariableSelectEvent(
    override val port: ComponentPort,
    val newVariable: ComponentPortVariableView?,
    val oldVariable: ComponentPortVariableView?,
) : ComponentPortEvent
