package net.voxelpi.vire.api.simulation.event.simulation.component.port

import net.voxelpi.vire.api.simulation.component.ComponentPort

data class ComponentPortDestroyEvent(
    override val port: ComponentPort,
) : ComponentPortEvent
