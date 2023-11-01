package net.voxelpi.vire.api.simulation.component

/**
 * The context of a state machine used when configuring a state machine parameter.
 */
interface StateMachineParameterContext {

    /**
     * The state machine.
     */
    val stateMachine: StateMachine

    /**
     * Gets the value of the given [parameter].
     */
    operator fun <T> get(parameter: StateMachineParameter<T>): T
}
