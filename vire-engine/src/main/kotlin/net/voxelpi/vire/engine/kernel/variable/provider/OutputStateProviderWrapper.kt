package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.Output
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

internal interface PartialOutputStateProviderWrapper : PartialOutputStateProvider {

    val outputStateProvider: PartialOutputStateProvider

    override val variableProvider: VariableProvider
        get() = outputStateProvider.variableProvider

    override fun get(output: OutputScalar): LogicState {
        return outputStateProvider[output]
    }

    override fun get(outputVector: OutputVector): Array<LogicState> {
        return outputStateProvider[outputVector]
    }

    override fun get(outputVector: OutputVector, index: Int): LogicState {
        return outputStateProvider[outputVector, index]
    }

    override fun hasValue(output: Output): Boolean {
        return outputStateProvider.hasValue(output)
    }

    override fun allOutputsSet(): Boolean {
        return outputStateProvider.allOutputsSet()
    }
}

internal interface MutablePartialOutputStateProviderWrapper : PartialOutputStateProviderWrapper, MutablePartialOutputStateProvider {

    override val outputStateProvider: MutablePartialOutputStateProvider

    override fun set(output: OutputScalar, value: LogicState) {
        outputStateProvider[output] = value
    }

    override fun set(outputVector: OutputVector, value: Array<LogicState>) {
        outputStateProvider[outputVector] = value
    }

    override fun set(outputVector: OutputVector, index: Int, value: LogicState) {
        outputStateProvider[outputVector, index] = value
    }
}

internal interface OutputStateProviderWrapper : OutputStateProvider {

    val outputStateProvider: OutputStateProvider

    override val variableProvider: VariableProvider
        get() = outputStateProvider.variableProvider

    override fun get(output: OutputScalar): LogicState {
        return outputStateProvider[output]
    }

    override fun get(outputVector: OutputVector): Array<LogicState> {
        return outputStateProvider[outputVector]
    }

    override fun get(outputVector: OutputVector, index: Int): LogicState {
        return outputStateProvider[outputVector, index]
    }
}

internal interface MutableOutputStateProviderWrapper : OutputStateProviderWrapper, MutableOutputStateProvider {

    override val outputStateProvider: MutableOutputStateProvider

    override fun set(output: OutputScalar, value: LogicState) {
        outputStateProvider[output] = value
    }

    override fun set(outputVector: OutputVector, value: Array<LogicState>) {
        outputStateProvider[outputVector] = value
    }

    override fun set(outputVector: OutputVector, index: Int, value: LogicState) {
        outputStateProvider[outputVector, index] = value
    }
}
