package net.voxelpi.vire.api.simulation.component

import net.voxelpi.vire.api.simulation.SimulationObject

/**
 * A component in a logic circuit.
 */
interface Component : SimulationObject {

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

    /**
     * Creates a new component port.
     */
    fun createPort(variableView: ComponentPortVariableView? = null): ComponentPort

    /**
     * Removes the given [port].
     */
    fun removePort(port: ComponentPort)
}
