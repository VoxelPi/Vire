package net.voxelpi.vire.api.circuit.event.network

import net.voxelpi.vire.api.circuit.Circuit
import net.voxelpi.vire.api.circuit.event.CircuitEvent
import net.voxelpi.vire.api.circuit.network.Network

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
