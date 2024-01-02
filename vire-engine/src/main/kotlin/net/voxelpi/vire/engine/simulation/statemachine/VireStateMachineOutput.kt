package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.simulation.statemachine.StateMachineOutput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineOutputVector
import net.voxelpi.vire.api.simulation.statemachine.StateMachineStateVector

/**
 * An output of a state machine.
 * @property name the name of the output.
 */
data class VireStateMachineOutput(
    override val name: String,
) : StateMachineOutput

/**
 * An output vector of a state machine.
 * @property name the name of the output vector.
 * @property initialSize the initial size of the output vector.
 */
data class VireStateMachineOutputVector(
    override val name: String,
    override val initialSize: StateMachineStateVector.InitialSizeProvider,
) : StateMachineOutputVector
