package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.simulation.statemachine.StateMachineIOState
import net.voxelpi.vire.api.simulation.statemachine.StateMachineOutput

/**
 * An output of a state machine.
 * @property name the name of the output.
 * @property initialSize the initial size of the output.
 */
data class VireStateMachineOutput(
    override val name: String,
    override val initialSize: StateMachineIOState.InitialSizeProvider,
) : StateMachineOutput
