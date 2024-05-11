package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableInputStateProvider

internal interface InputStateStorageWrapper : InputStateProvider {

    val inputStateStorage: InputStateStorage

    override val variableProvider: VariableProvider
        get() = inputStateStorage.variableProvider

    override fun get(input: InputScalar): LogicState {
        return inputStateStorage[input]
    }

    override fun get(inputVector: InputVector): Array<LogicState> {
        return inputStateStorage[inputVector]
    }

    override fun get(inputVector: InputVector, index: Int): LogicState {
        return inputStateStorage[inputVector, index]
    }
}

internal interface MutableInputStateStorageWrapper : InputStateStorageWrapper, MutableInputStateProvider {

    override val inputStateStorage: MutableInputStateStorage

    override fun set(input: InputScalar, value: LogicState) {
        inputStateStorage[input] = value
    }

    override fun set(inputVector: InputVector, value: Array<LogicState>) {
        inputStateStorage[inputVector] = value
    }

    override fun set(inputVector: InputVector, index: Int, value: LogicState) {
        inputStateStorage[inputVector, index] = value
    }
}
