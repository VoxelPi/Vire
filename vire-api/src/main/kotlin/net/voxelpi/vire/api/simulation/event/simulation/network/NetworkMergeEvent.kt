package net.voxelpi.vire.api.simulation.event.simulation.network

import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.api.simulation.event.SimulationEvent
import net.voxelpi.vire.api.simulation.network.Network

data class NetworkMergeEvent(
    override val simulation: Simulation,
    val network: Network,
    val mergedNetworks: List<Network>,
) : SimulationEvent
