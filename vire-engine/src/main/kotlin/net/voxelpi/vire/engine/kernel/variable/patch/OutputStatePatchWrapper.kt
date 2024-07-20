package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.Output
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialOutputStateProvider

internal interface OutputStatePatchWrapper : PartialOutputStateProvider {

    val outputStatePatch: OutputStatePatch

    override val variableProvider: VariableProvider
        get() = outputStatePatch.variableProvider

    override fun get(output: OutputScalar): LogicState {
        return outputStatePatch[output]
    }

    override fun get(outputVector: OutputVector): Array<LogicState> {
        return outputStatePatch[outputVector]
    }

    override fun get(outputVector: OutputVector, index: Int): LogicState {
        return outputStatePatch[outputVector, index]
    }

    override fun hasValue(output: Output): Boolean {
        return outputStatePatch.hasValue(output)
    }

    override fun allOutputsSet(): Boolean {
        return outputStatePatch.allOutputsSet()
    }
}

internal interface MutableOutputStatePatchWrapper : OutputStatePatchWrapper, MutablePartialOutputStateProvider {

    override val outputStatePatch: MutableOutputStatePatch

    override fun set(output: OutputScalar, value: LogicState) {
        outputStatePatch[output] = value
    }

    override fun set(outputVector: OutputVector, value: Array<LogicState>) {
        outputStatePatch[outputVector] = value
    }

    override fun set(outputVector: OutputVector, index: Int, value: LogicState) {
        outputStatePatch[outputVector, index] = value
    }
}
