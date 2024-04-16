package net.voxelpi.vire.engine.circuit.statemachine

import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.StateMachineInput
import net.voxelpi.vire.api.circuit.statemachine.StateMachineOutput
import net.voxelpi.vire.api.circuit.statemachine.StateMachineParameter
import net.voxelpi.vire.api.circuit.statemachine.StateMachineUpdateContext
import net.voxelpi.vire.api.circuit.statemachine.StateMachineVariable

class VireStateMachineUpdateContext(
    private val instance: VireStateMachineInstance,
) : StateMachineUpdateContext {

    override val stateMachine: StateMachine
        get() = instance.stateMachine

    override fun size(input: StateMachineInput): Int {
        return instance.size(input)
    }

    override fun size(output: StateMachineOutput): Int {
        return instance.size(output)
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

    override fun get(input: StateMachineInput, index: Int): LogicState {
        return instance[input, index]
    }

    override fun vector(input: StateMachineInput): Array<LogicState> {
        return instance.vector(input)
    }

    override fun set(output: StateMachineOutput, index: Int, value: LogicState) {
        instance[output, index] = value
    }

    override fun vector(output: StateMachineOutput, value: Array<LogicState>) {
        instance.vector(output, value)
    }
}
