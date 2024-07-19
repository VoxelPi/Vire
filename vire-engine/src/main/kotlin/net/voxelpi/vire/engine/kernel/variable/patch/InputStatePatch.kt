package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.Input
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.InputStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.MutableInputStateMap

internal interface InputStatePatch : PartialInputStateProvider {

    override val variableProvider: VariableProvider

    val data: InputStateMap

    fun copy(): InputStatePatch

    fun mutableCopy(): MutableInputStatePatch

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
}

internal class MutableInputStatePatch(
    override val variableProvider: VariableProvider,
    override val data: MutableInputStateMap,
) : InputStatePatch, MutablePartialInputStateProvider {

    override fun copy(): InputStatePatch = mutableCopy()

    override fun mutableCopy(): MutableInputStatePatch {
        return MutableInputStatePatch(variableProvider, data.toMutableMap())
    }

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

internal fun inputStateStorage(variableProvider: VariableProvider, data: InputStateMap): InputStatePatch {
    return mutableInputStateStorage(variableProvider, data)
}

internal fun inputStateStorage(variableProvider: VariableProvider, dataProvider: InputStateProvider): InputStatePatch {
    return mutableInputStateStorage(variableProvider, dataProvider)
}

internal fun mutableInputStateStorage(variableProvider: VariableProvider, data: InputStateMap): MutableInputStatePatch {
    val processedData: MutableInputStateMap = mutableMapOf()
    for (input in variableProvider.inputs()) {
        // Check that the input has an assigned value.
        require(input.name in data) { "No value provided for the input ${input.name}" }

        // Get the value from the map.
        val value = data[input.name]!!

        // Put value into map.
        processedData[input.name] = value
    }
    return MutableInputStatePatch(variableProvider, processedData)
}

internal fun mutableInputStateStorage(variableProvider: VariableProvider, dataProvider: InputStateProvider): MutableInputStatePatch {
    val processedData: MutableInputStateMap = mutableMapOf()
    for (input in variableProvider.inputs()) {
        // Check that the input has an assigned value.
        require(dataProvider.variableProvider.hasVariable(input)) { "No value provided for the input ${input.name}" }

        // Get the value from the provider.
        val value = when (input) {
            is InputScalar -> arrayOf(dataProvider[input])
            is InputVector -> dataProvider[input]
            is InputVectorElement -> throw IllegalArgumentException("Input vector elements may not be specified ('${input.name}')")
        }

        // Put value into map.
        processedData[input.name] = value
    }
    return MutableInputStatePatch(variableProvider, processedData)
}
