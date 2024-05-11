package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProvider

internal interface OutputStateStorageWrapper : OutputStateProvider {

    val outputStateStorage: OutputStateStorage

    override val variableProvider: VariableProvider
        get() = outputStateStorage.variableProvider

    override fun get(output: OutputScalar): LogicState {
        return outputStateStorage[output]
    }

    override fun get(outputVector: OutputVector): Array<LogicState> {
        return outputStateStorage[outputVector]
    }

    override fun get(outputVector: OutputVector, index: Int): LogicState {
        return outputStateStorage[outputVector, index]
    }
}

internal interface MutableOutputStateStorageWrapper : OutputStateStorageWrapper, MutableOutputStateProvider {

    override val outputStateStorage: MutableOutputStateStorage

    override fun set(output: OutputScalar, value: LogicState) {
        outputStateStorage[output] = value
    }

    override fun set(outputVector: OutputVector, value: Array<LogicState>) {
        outputStateStorage[outputVector] = value
    }

    override fun set(outputVector: OutputVector, index: Int, value: LogicState) {
        outputStateStorage[outputVector, index] = value
    }
}
