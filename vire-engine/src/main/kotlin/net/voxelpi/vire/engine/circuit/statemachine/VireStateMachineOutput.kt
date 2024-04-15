package net.voxelpi.vire.engine.circuit.statemachine

import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.circuit.statemachine.StateMachineIOState
import net.voxelpi.vire.api.circuit.statemachine.StateMachineOutput

/**
 * An output of a state machine.
 * @property name the name of the output.
 * @property initialSize the initial size of the output.
 * @property initialValue the initial value of the output.
 */
data class VireStateMachineOutput(
    override val name: String,
    override val initialSize: StateMachineIOState.InitialSizeProvider,
    override val initialValue: LogicState,
) : StateMachineOutput
