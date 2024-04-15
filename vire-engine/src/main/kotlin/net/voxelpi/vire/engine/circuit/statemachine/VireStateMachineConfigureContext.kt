package net.voxelpi.vire.engine.circuit.statemachine

import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.StateMachineConfigureContext
import net.voxelpi.vire.api.circuit.statemachine.StateMachineInput
import net.voxelpi.vire.api.circuit.statemachine.StateMachineOutput
import net.voxelpi.vire.api.circuit.statemachine.StateMachineParameter
import net.voxelpi.vire.api.circuit.statemachine.StateMachineVariable

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
