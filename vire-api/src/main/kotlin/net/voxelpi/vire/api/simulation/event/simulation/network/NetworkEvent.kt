package net.voxelpi.vire.api.simulation.event.simulation.network

import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.api.simulation.event.SimulationEvent
import net.voxelpi.vire.api.simulation.network.Network

interface NetworkEvent : SimulationEvent {

    val network: Network

    override val simulation: Simulation
        get() = network.simulation
}
