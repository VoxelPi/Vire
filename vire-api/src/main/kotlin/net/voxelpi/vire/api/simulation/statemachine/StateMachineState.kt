package net.voxelpi.vire.api.simulation.statemachine

import net.voxelpi.vire.api.simulation.component.ComponentPortVector

sealed interface StateMachineState {

    /**
     * The name of the state.
     */
    val name: String
}

sealed interface StateMachineIOState : StateMachineState, ComponentPortVector {

    /**
     * The initial size of the state vector.
     */
    val initialSize: InitialSizeProvider

    /**
     * The provider of the initial size.
     */
    sealed interface InitialSizeProvider {

        /**
         * The initial size is a constant number.
         */
        data class Value(val value: Int) : InitialSizeProvider

        /**
         * The initial size is the value of the given parameter.
         */
        data class Parameter(val parameter: StateMachineParameter<out Number>) : InitialSizeProvider
    }
}
