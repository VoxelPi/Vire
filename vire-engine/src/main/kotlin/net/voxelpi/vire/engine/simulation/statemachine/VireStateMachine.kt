package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineConfigureContext
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineOutput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineParameter
import net.voxelpi.vire.api.simulation.statemachine.StateMachineUpdateContext
import net.voxelpi.vire.api.simulation.statemachine.StateMachineVariable

class VireStateMachine : StateMachine {


    override val parameters: Map<String, StateMachineParameter<*>>
        get() = TODO("Not yet implemented")
    override val variables: Map<String, StateMachineVariable<*>>
        get() = TODO("Not yet implemented")
    override val inputs: Map<String, StateMachineInput>
        get() = TODO("Not yet implemented")
    override val outputs: Map<String, StateMachineOutput>
        get() = TODO("Not yet implemented")

    class Builder(
        override val id: Identifier,
    ) : StateMachine.Builder {

        override fun declareParameter() {
            TODO("Not yet implemented")
        }

        override var configure: (StateMachineConfigureContext) -> Unit = {}

        override var update: (StateMachineUpdateContext) -> Unit = {}

        override fun create(): VireStateMachine {
            TODO("Not yet implemented")
        }
    }
}
