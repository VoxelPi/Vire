package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.Input
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialInputStateProvider

internal interface InputStatePatchWrapper : PartialInputStateProvider {

    val inputStatePatch: InputStatePatch

    override val variableProvider: VariableProvider
        get() = inputStatePatch.variableProvider

    override fun get(input: InputScalar): LogicState {
        return inputStatePatch[input]
    }

    override fun get(inputVector: InputVector): Array<LogicState> {
        return inputStatePatch[inputVector]
    }

    override fun get(inputVector: InputVector, index: Int): LogicState {
        return inputStatePatch[inputVector, index]
    }

    override fun hasValue(input: Input): Boolean {
        return inputStatePatch.hasValue(input)
    }

    override fun allInputsSet(): Boolean {
        return inputStatePatch.allInputsSet()
    }
}

internal interface MutableInputStatePatchWrapper : InputStatePatchWrapper, MutablePartialInputStateProvider {

    override val inputStatePatch: MutableInputStatePatch

    override fun set(input: InputScalar, value: LogicState) {
        inputStatePatch[input] = value
    }

    override fun set(inputVector: InputVector, value: Array<LogicState>) {
        inputStatePatch[inputVector] = value
    }

    override fun set(inputVector: InputVector, index: Int, value: LogicState) {
        inputStatePatch[inputVector, index] = value
    }
}
