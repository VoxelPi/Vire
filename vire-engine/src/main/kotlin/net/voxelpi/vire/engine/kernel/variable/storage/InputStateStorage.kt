package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableInputStateProvider

internal typealias InputStateMap = Map<String, Array<LogicState>>

internal typealias MutableInputStateMap = MutableMap<String, Array<LogicState>>

internal interface InputStateStorage : InputStateProvider {

    override val variableProvider: VariableProvider

    val data: InputStateMap

    fun copy(): InputStateStorage

    fun mutableCopy(): MutableInputStateStorage

    override fun get(input: InputScalar): LogicState {
        // Check that an input with the given name exists.
        require(variableProvider.hasInput(input.name)) { "Unknown input ${input.name}" }

        // Return the value of the input.
        return data[input.name]!![0]
    }

    override fun get(inputVector: InputVector): Array<LogicState> {
        // Check that an input with the given name exists.
        require(variableProvider.hasInput(inputVector.name)) { "Unknown input vector ${inputVector.name}" }

        // Return the value of the input.
        return data[inputVector.name]!!
    }

    override fun get(inputVector: InputVector, index: Int): LogicState {
        return get(inputVector)[index]
    }
}

internal class MutableInputStateStorage(
    override val variableProvider: VariableProvider,
    override val data: MutableInputStateMap,
) : InputStateStorage, MutableInputStateProvider {

    override fun copy(): MutableInputStateStorage = mutableCopy()

    override fun mutableCopy(): MutableInputStateStorage {
        return MutableInputStateStorage(variableProvider, data.toMutableMap())
    }

    override fun set(input: InputScalar, value: LogicState) {
        // Check that an input with the given name exists.
        require(variableProvider.hasInput(input.name)) { "Unknown input ${input.name}" }

        // Update the value of the input.
        data[input.name]!![0] = value
    }

    override fun set(inputVector: InputVector, value: Array<LogicState>) {
        // Check that an input with the given name exists.
        require(variableProvider.hasInput(inputVector.name)) { "Unknown input vector ${inputVector.name}" }

        // Update the value of the input.
        data[inputVector.name] = value
    }

    override fun set(inputVector: InputVector, index: Int, value: LogicState) {
        // Check that an input with the given name exists.
        require(variableProvider.hasInput(inputVector.name)) { "Unknown input vector ${inputVector.name}" }

        // Return the value of the input.
        data[inputVector.name]!![index] = value
    }
}

internal fun inputStateStorage(variableProvider: VariableProvider, data: InputStateMap): InputStateStorage {
    return mutableInputStateStorage(variableProvider, data)
}

internal fun inputStateStorage(variableProvider: VariableProvider, dataProvider: InputStateProvider): InputStateStorage {
    return mutableInputStateStorage(variableProvider, dataProvider)
}

internal fun mutableInputStateStorage(variableProvider: VariableProvider, data: InputStateMap): MutableInputStateStorage {
    val processedData: MutableInputStateMap = mutableMapOf()
    for (input in variableProvider.inputs()) {
        // Check that the input has an assigned value.
        require(input.name in data) { "No value provided for the input ${input.name}" }

        // Get the value from the map.
        val value = data[input.name]!!

        // Put value into map.
        processedData[input.name] = value
    }
    return MutableInputStateStorage(variableProvider, processedData)
}

internal fun mutableInputStateStorage(variableProvider: VariableProvider, dataProvider: InputStateProvider): MutableInputStateStorage {
    val processedData: MutableInputStateMap = mutableMapOf()
    for (input in variableProvider.inputs()) {
        // Check that the input has an assigned value.
        require(dataProvider.variableProvider.hasVariable(input)) { "No value provided for the input ${input.name}" }

        // Get the value from the provider.
        val value = when (input) {
            is InputScalar -> arrayOf(dataProvider[input])
            is InputVector -> dataProvider[input]
            else -> throw IllegalStateException()
        }

        // Put value into map.
        processedData[input.name] = value
    }
    return MutableInputStateStorage(variableProvider, processedData)
}
