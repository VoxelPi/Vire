package net.voxelpi.vire.api.simulation.statemachine

sealed interface StateMachineState {

    /**
     * The name of the state.
     */
    val name: String
}

sealed interface StateMachineStateVector {

    /**
     * The name of the state vector.
     */
    val name: String

    /**
     * The initial size of the state.
     */
    val initialSize: InitialSizeProvider

    sealed interface InitialSizeProvider {
        data class Value(val value: Int) : InitialSizeProvider
        data class Parameter(val parameter: StateMachineParameter<Number>) : InitialSizeProvider
    }
}

sealed interface StateMachineIOState : StateMachineState

sealed interface StateMachineIOStateVector : StateMachineStateVector
