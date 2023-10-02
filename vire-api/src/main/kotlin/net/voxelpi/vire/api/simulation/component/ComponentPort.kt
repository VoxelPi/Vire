package net.voxelpi.vire.api.simulation.component

import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.api.simulation.network.Network
import net.voxelpi.vire.api.simulation.network.NetworkNode
import net.voxelpi.vire.api.simulation.network.NetworkState
import java.util.UUID

/**
 * An interface of a component.
 * Interfaces the specified state machine input or output to connected network.
 */
interface ComponentPort {

    /**
     * The simulation the network belongs to.
     */
    val simulation: Simulation

    /**
     * The component the port belongs to.
     */
    val component: Component

    /**
     * The unique id of the component port.
     */
    val uniqueId: UUID

    /**
     * The variable that should be bound to the interface.
     */
    var variableView: ComponentPortVariableView?

    /**
     * The network node of the component port.
     */
    val node: NetworkNode

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
