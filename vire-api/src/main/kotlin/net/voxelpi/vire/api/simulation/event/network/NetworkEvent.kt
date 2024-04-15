package net.voxelpi.vire.api.simulation.event.network

import net.voxelpi.vire.api.simulation.Circuit
import net.voxelpi.vire.api.simulation.event.CircuitEvent
import net.voxelpi.vire.api.simulation.network.Network

/**
 * An event that affect a network.
 */
interface NetworkEvent : CircuitEvent {

    /**
     * The affected network.
     */
    val network: Network

    override val circuit: Circuit
        get() = network.circuit
}
