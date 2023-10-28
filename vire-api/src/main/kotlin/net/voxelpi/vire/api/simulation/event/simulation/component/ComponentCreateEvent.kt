package net.voxelpi.vire.api.simulation.event.simulation.component

import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.event.SimulationEvent

data class ComponentCreateEvent(
    override val simulation: Simulation,
    val component: Component,
) : SimulationEvent
