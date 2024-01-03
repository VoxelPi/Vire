package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineConfigureContext
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineOutput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineParameter
import net.voxelpi.vire.api.simulation.statemachine.StateMachineVariable

class VireStateMachineConfigureContext(
    private val instance: VireStateMachineInstance,
) : StateMachineConfigureContext {

    override val stateMachine: StateMachine
        get() = instance.stateMachine

    override fun size(input: StateMachineInput): Int {
        return instance.size(input)
    }

    override fun size(output: StateMachineOutput): Int {
        return instance.size(output)
    }

    override fun resize(input: StateMachineInput, size: Int) {
        instance.resize(input, size)
    }

    override fun resize(output: StateMachineOutput, size: Int) {
        instance.resize(output, size)
    }

    override fun <T> get(parameter: StateMachineParameter<T>): T {
        return instance[parameter]
    }

    override fun <T> get(variable: StateMachineVariable<T>): T {
        return instance[variable]
    }

    override fun <T> set(variable: StateMachineVariable<T>, value: T) {
        instance[variable] = value
    }

    override fun set(output: StateMachineOutput, index: Int, value: LogicState) {
        instance[output, index] = value
    }
}
