package net.voxelpi.vire.engine.circuit.statemachine

import net.voxelpi.vire.api.circuit.statemachine.StateMachineIOState
import net.voxelpi.vire.api.circuit.statemachine.StateMachineInput

/**
 * An input of a state machine.
 * @property name the name of the input.
 * @property initialSize the initial size of the input.
 */
data class VireStateMachineInput(
    override val name: String,
    override val initialSize: StateMachineIOState.InitialSizeProvider,
) : StateMachineInput
