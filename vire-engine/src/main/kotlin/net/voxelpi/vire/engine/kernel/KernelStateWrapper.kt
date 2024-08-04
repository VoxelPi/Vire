package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider

internal interface KernelStateWrapper :
    VectorSizeProvider,
    ParameterStateProvider,
    SettingStateProvider,
    FieldStateProvider,
    InputStateProvider,
    OutputStateProvider {

    val kernelState: KernelState

    override val variableProvider: VariableProvider
        get() = kernelState.variableProvider

    override fun size(vector: VectorVariable<*>): Int = kernelState.size(vector)

    override fun size(vectorName: String): Int = kernelState.size(vectorName)

    override fun <T> get(parameter: Parameter<T>): T = kernelState[parameter]

    override fun <T> get(setting: Setting<T>): T = kernelState[setting]

    override fun <T> get(field: Field<T>): T = kernelState[field]

    override fun get(input: InputScalar): LogicState = kernelState[input]

    override fun get(inputVector: InputVector): Array<LogicState> = kernelState[inputVector]

    override fun get(inputVector: InputVector, index: Int): LogicState = kernelState[inputVector, index]

    override fun get(output: OutputScalar): LogicState = kernelState[output]

    override fun get(outputVector: OutputVector): Array<LogicState> = kernelState[outputVector]

    override fun get(outputVector: OutputVector, index: Int): LogicState = kernelState[outputVector, index]
}

internal interface MutableKernelStateWrapper :
    KernelStateWrapper,
    MutableFieldStateProvider,
    MutableInputStateProvider,
    MutableOutputStateProvider {

    override val kernelState: MutableKernelState

    override fun <T> set(field: Field<T>, value: T) = kernelState.set(field, value)

    override fun set(input: InputScalar, value: LogicState) = kernelState.set(input, value)

    override fun set(inputVector: InputVector, value: Array<LogicState>) = kernelState.set(inputVector, value)

    override fun set(inputVector: InputVector, index: Int, value: LogicState) = kernelState.set(inputVector, index, value)

    override fun set(output: OutputScalar, value: LogicState) = kernelState.set(output, value)

    override fun set(outputVector: OutputVector, value: Array<LogicState>) = kernelState.set(outputVector, value)

    override fun set(outputVector: OutputVector, index: Int, value: LogicState) = kernelState.set(outputVector, index, value)
}
