package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.simulation.statemachine.StateMachineInput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInputVector
import net.voxelpi.vire.api.simulation.statemachine.StateMachineStateVector

/**
 * An input of a state machine.
 * @property name the name of the input.
 */
data class VireStateMachineInput(
    override val name: String,
) : StateMachineInput

/**
 * An input vector of a state machine.
 * @property name the name of the input vector.
 * @property initialSize the initial size of the input vector.
 */
data class VireStateMachineInputVector(
    override val name: String,
    override val initialSize: StateMachineStateVector.InitialSizeProvider,
) : StateMachineInputVector
