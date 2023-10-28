package net.voxelpi.vire.api.simulation.event.simulation.network

import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.api.simulation.event.SimulationEvent
import net.voxelpi.vire.api.simulation.network.Network

data class NetworkCreateEvent(
    override val simulation: Simulation,
    val network: Network,
) : SimulationEvent
