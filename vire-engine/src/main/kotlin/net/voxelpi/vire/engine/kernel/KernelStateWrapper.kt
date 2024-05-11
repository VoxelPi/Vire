package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.FieldProvider
import net.voxelpi.vire.engine.kernel.variable.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.InputProvider
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.MutableFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.MutableInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.MutableOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.OutputProvider
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.OutputVector

internal interface KernelStateWrapper : KernelInstanceWrapper, FieldStateProvider, InputStateProvider, OutputStateProvider {

    val kernelState: KernelState

    override val kernelInstance: KernelInstance

    override val kernelVariant: KernelVariant
        get() = kernelInstance.kernelVariant

    override val fieldProvider: FieldProvider
        get() = kernelVariant

    override val inputProvider: InputProvider
        get() = kernelVariant

    override val outputProvider: OutputProvider
        get() = kernelVariant

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

    override val kernelInstance: KernelInstance
        get() = kernelState.kernelInstance

    override fun <T> set(field: Field<T>, value: T) = kernelState.set(field, value)

    override fun set(input: InputScalar, value: LogicState) = kernelState.set(input, value)

    override fun set(inputVector: InputVector, value: Array<LogicState>) = kernelState.set(inputVector, value)

    override fun set(inputVector: InputVector, index: Int, value: LogicState) = kernelState.set(inputVector, index, value)

    override fun set(output: OutputScalar, value: LogicState) = kernelState.set(output, value)

    override fun set(outputVector: OutputVector, value: Array<LogicState>) = kernelState.set(outputVector, value)

    override fun set(outputVector: OutputVector, index: Int, value: LogicState) = kernelState.set(outputVector, index, value)
}
