package net.voxelpi.vire.api.circuit.component

import net.voxelpi.vire.api.circuit.CircuitElement
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.StateMachineInstance

/**
 * A component in a logic circuit.
 */
interface Component : CircuitElement {

    /**
     * The state machine of the component.
     */
    val stateMachine: StateMachine

    /**
     * The state machine instance of the component.
     */
    val stateMachineInstance: StateMachineInstance

    /**
     * Returns a collection of all ports that belong to the component.
     */
    fun ports(): Collection<ComponentPort>

    /**
     * Creates a new component port that has the given [variable] assigned to it.
     */
    fun createPort(variable: ComponentPortVectorVariable? = null): ComponentPort

    /**
     * Removes the given [port].
     */
    fun removePort(port: ComponentPort)

    /**
     * Resets the state machine of the component.
     * If [parameters] is set to `true`, the parameters of the state machine are also reset to their initial values,
     * otherwise they are left unchanged.
     */
    fun reset(parameters: Boolean = false)
}
