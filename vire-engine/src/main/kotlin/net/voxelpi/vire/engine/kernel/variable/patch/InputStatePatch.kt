package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.Input
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.UninitializedVariableException
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.InputStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.InputStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableInputStateMap

internal open class InputStatePatch(
    final override val variableProvider: VariableProvider,
    initialData: InputStateMap = emptyMap(),
) : PartialInputStateProvider {

    init {
        for ((inputName, inputState) in initialData) {
            val input = variableProvider.input(inputName)
                ?: throw IllegalStateException("Data specified for unknown input \"$inputName\".")

            for (channelState in inputState) {
                require(input.isValidValue(channelState)) { "Invalid value specified for input \"$inputName\"." }
            }
        }
    }

    protected open val data: InputStateMap = initialData.toMap()

    constructor(variableProvider: VariableProvider, initialData: PartialInputStateProvider) : this(
        variableProvider,
        variableProvider.inputs().filter { initialData.hasValue(it) }.associate { it.name to initialData.vector(it)!! }
    )

    fun copy(): InputStatePatch {
        return InputStatePatch(variableProvider, data)
    }

    fun mutableCopy(): MutableInputStatePatch {
        return MutableInputStatePatch(variableProvider, data)
    }

    override fun get(input: InputScalar): LogicState {
        // Check that an input with the given name exists.
        require(variableProvider.hasInput(input)) { "Unknown input ${input.name}" }

        // Check that the input has been initialized.
        if (input.name !in data) {
            throw UninitializedVariableException(input)
        }

        // Return the value of the input.
        return data[input.name]!![0]
    }

    override fun get(inputVector: InputVector): Array<LogicState> {
        // Check that an input with the given name exists.
        require(variableProvider.hasInput(inputVector)) { "Unknown input vector ${inputVector.name}" }

        // Check that the input vector has been initialized.
        if (inputVector.name !in data) {
            throw UninitializedVariableException(inputVector)
        }

        // Return the value of the input.
        return data[inputVector.name]!!
    }

    override fun get(inputVector: InputVector, index: Int): LogicState {
        return get(inputVector)[index]
    }

    override fun hasValue(input: Input): Boolean {
        return input.name in data
    }

    override fun allInputsSet(): Boolean {
        return variableProvider.inputs().all { hasValue(it) }
    }

    /**
     * Creates an input state storage using the set data.
     * All inputs must have a set value otherwise this operation fails.
     */
    fun createStorage(): InputStateStorage {
        return InputStateStorage(variableProvider, data)
    }
}

internal class MutableInputStatePatch(
    variableProvider: VariableProvider,
    initialData: InputStateMap = emptyMap(),
) : InputStatePatch(variableProvider, initialData), MutablePartialInputStateProvider {

    override val data: MutableInputStateMap = initialData.toMutableMap()

    constructor(variableProvider: VariableProvider, initialData: PartialInputStateProvider) : this(
        variableProvider,
        variableProvider.inputs().filter { initialData.hasValue(it) }.associate { it.name to initialData.vector(it)!! }
    )

    override fun set(input: InputScalar, value: LogicState) {
        // Check that an input with the given name exists.
        require(variableProvider.hasInput(input)) { "Unknown input ${input.name}" }

        // Update the value of the input.
        data[input.name] = arrayOf(value)
    }

    override fun set(inputVector: InputVector, value: Array<LogicState>) {
        // Check that an input with the given name exists.
        require(variableProvider.hasInput(inputVector)) { "Unknown input vector ${inputVector.name}" }

        // Update the value of the input.
        data[inputVector.name] = value
    }

    override fun set(inputVector: InputVector, index: Int, value: LogicState) {
        // Check that an input with the given name exists.
        require(variableProvider.hasInput(inputVector)) { "Unknown input vector ${inputVector.name}" }

        // Return the value of the input.
        data[inputVector.name]!![index] = value
    }
}
