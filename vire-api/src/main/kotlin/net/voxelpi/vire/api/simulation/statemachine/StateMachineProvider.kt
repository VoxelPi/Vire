package net.voxelpi.vire.api.simulation.statemachine

/**
 * Provides a state machine.
 */
interface StateMachineProvider {

    /**
     * The provided state machine.
     */
    val stateMachine: StateMachine

    /**
     * Creates a new instance of the provided state machine.
     * The parameters of the instance are configured using the specified [configuration].
     */
    fun createInstance(
        configuration: StateMachineInstance.ConfigurationContext.() -> Unit = {},
    ): StateMachineInstance {
        return stateMachine.createInstance(configuration)
    }

    /**
     * Creates a new instance of the provided state machine.
     * The parameters of the instance are configured using the specified [configuration].
     * Whilst Not all parameters must be specified, only existing parameters may be specified.
     */
    fun createInstance(
        configuration: Map<String, Any?>,
    ): StateMachineInstance {
        return stateMachine.createInstance(configuration)
    }
}
