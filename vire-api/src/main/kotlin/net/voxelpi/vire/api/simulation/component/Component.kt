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
     * Queries the current value of the given [parameter].
     */
    fun <T> parameter(parameter: StateMachineParameter<T>): T

    /**
     * Sets the value of the given [parameter] to the given [value].
     * If the new value doesn't satisfy all predicates, `false` is returned, otherwise `true` is returned.
     */
    fun <T> parameter(parameter: StateMachineParameter<T>, value: T): Boolean

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
