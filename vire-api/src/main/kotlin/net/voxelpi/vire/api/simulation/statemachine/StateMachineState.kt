package net.voxelpi.vire.api.simulation.statemachine

sealed interface StateMachineState {

    /**
     * The name of the state.
     */
    val name: String
}

sealed interface StateMachineIOState : StateMachineState {

    /**
     * The initial size of the state vector.
     */
    val initialSize: InitialSizeProvider

    sealed interface InitialSizeProvider {
        data class Value(val value: Int) : InitialSizeProvider
        data class Parameter(val parameter: StateMachineParameter<out Number>) : InitialSizeProvider
    }
}
