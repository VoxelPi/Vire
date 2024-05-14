package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

internal interface InputStateProviderWrapper : InputStateProvider {

    val inputStateProvider: InputStateProvider

    override val variableProvider: VariableProvider
        get() = inputStateProvider.variableProvider

    override fun get(input: InputScalar): LogicState {
        return inputStateProvider[input]
    }

    override fun get(inputVector: InputVector): Array<LogicState> {
        return inputStateProvider[inputVector]
    }

    override fun get(inputVector: InputVector, index: Int): LogicState {
        return inputStateProvider[inputVector, index]
    }
}

internal interface MutableInputStateProviderWrapper : InputStateProviderWrapper, MutableInputStateProvider {

    override val inputStateProvider: MutableInputStateProvider

    override fun set(input: InputScalar, value: LogicState) {
        inputStateProvider[input] = value
    }

    override fun set(inputVector: InputVector, value: Array<LogicState>) {
        inputStateProvider[inputVector] = value
    }

    override fun set(inputVector: InputVector, index: Int, value: LogicState) {
        inputStateProvider[inputVector, index] = value
    }
}
