package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.Input
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.InputStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.InputStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableInputStateMap

internal open class InputStatePatch(
    final override val variableProvider: VariableProvider,
    initialData: InputStateMap,
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

        // Return the value of the input.
        return data[input.name]!![0]
    }

    override fun get(inputVector: InputVector): Array<LogicState> {
        // Check that an input with the given name exists.
        require(variableProvider.hasInput(inputVector)) { "Unknown input vector ${inputVector.name}" }

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
    initialData: InputStateMap,
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
        data[input.name]!![0] = value
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

    fun update(data: InputStateMap) {
        for ((inputName, value) in data) {
            // Check that only existing inputs are specified.
            val input = variableProvider.input(inputName)
                ?: throw IllegalArgumentException("Unknown input '$inputName'")

            // Update the value of the input.
            when (input) {
                is InputScalar -> this[input] = value[0]
                is InputVector -> this[input] = value
                is InputVectorElement -> throw IllegalArgumentException("Input vector elements may not be specified ('$inputName')")
            }
        }
    }
}
