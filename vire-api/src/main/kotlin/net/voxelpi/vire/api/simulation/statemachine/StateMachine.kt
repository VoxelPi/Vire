package net.voxelpi.vire.api.simulation.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.Vire

interface StateMachine {

    /**
     * The id of the state machine.
     */
    val id: Identifier

    /**
     * All registered parameters of the state machine.
     */
    val parameters: Map<String, StateMachineParameter<*>>

    /**
     * All registered variables of the state machine.
     */
    val variables: Map<String, StateMachineVariable<*>>

    /**
     * All registered inputs of the state machine.
     */
    val inputs: Map<String, StateMachineInput>

    /**
     * All registered outputs of the state machine.
     */
    val outputs: Map<String, StateMachineOutput>

    interface Builder {

        /**
         * The id of the state machine.
         */
        val id: Identifier

        var initialize: () -> Unit

        var update: () -> Unit

        fun declareParameter()

        /**
         * Creates the state machine.
         */
        fun create(): StateMachine
    }

    companion object {

        fun create(id: Identifier, init: Builder.() -> Unit): StateMachine {
            return Vire.stateMachineFactory.get().create(id, init)
        }
    }
}
