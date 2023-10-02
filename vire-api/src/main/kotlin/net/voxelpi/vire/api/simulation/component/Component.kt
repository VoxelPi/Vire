package net.voxelpi.vire.api.simulation.component

import net.voxelpi.vire.api.simulation.Simulation
import java.util.UUID

/**
 * A component in a logic circuit.
 */
interface Component {

    /**
     * The simulation the component belongs to.
     */
    val simulation: Simulation

    /**
     * The unique id of the component.
     */
    val uniqueId: UUID

    /**
     * The state machine of the component.
     */
    val stateMachine: StateMachine

    /**
     * The state machine context of the component.
     */
    val stateMachineContext: StateMachineContext

    /**
     * Returns a collection of all ports that belong to the component.
     */
    fun ports(): Collection<ComponentPort>
}
