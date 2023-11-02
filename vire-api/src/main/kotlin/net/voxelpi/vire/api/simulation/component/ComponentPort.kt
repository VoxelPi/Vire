package net.voxelpi.vire.api.simulation.component

import net.voxelpi.vire.api.simulation.SimulationObject
import net.voxelpi.vire.api.simulation.network.Network
import net.voxelpi.vire.api.simulation.network.NetworkNodeHolder
import net.voxelpi.vire.api.simulation.network.NetworkState

/**
 * A port of a component.
 * Interfaces the specified state machine input or output to connected network.
 */
interface ComponentPort : SimulationObject, NetworkNodeHolder {

    /**
     * The component the port belongs to.
     */
    val component: Component

    /**
     * The variable that should be bound to the port.
     */
    var variable: ComponentPortVectorVariable?

    /**
     * The network of the component port.
     */
    val network: Network

    /**
     * If the port has an output variable assigned to it, the state of that output variable is pushed to the [node] network
     * and the resulting [NetworkState] is returned.
     * Otherwise, nothing happens and null is returned.
     */
    fun pushOutput(): NetworkState?
}
