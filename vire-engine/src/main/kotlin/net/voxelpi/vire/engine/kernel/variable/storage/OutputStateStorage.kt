package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.OutputVectorElement
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProvider

internal typealias OutputStateMap = Map<String, Array<LogicState>>

internal typealias MutableOutputStateMap = MutableMap<String, Array<LogicState>>

internal interface OutputStateStorage : OutputStateProvider {

    override val variableProvider: VariableProvider

    val data: OutputStateMap

    fun copy(): OutputStateStorage

    fun mutableCopy(): MutableOutputStateStorage

    override fun get(output: OutputScalar): LogicState {
        // Check that an output with the given name exists.
        require(variableProvider.hasOutput(output)) { "Unknown output ${output.name}" }

        // Return the value of the output.
        return data[output.name]!![0]
    }

    override fun get(outputVector: OutputVector): Array<LogicState> {
        // Check that an output with the given name exists.
        require(variableProvider.hasOutput(outputVector)) { "Unknown output vector ${outputVector.name}" }

        // Return the value of the output.
        return data[outputVector.name]!!
    }

    override fun get(outputVector: OutputVector, index: Int): LogicState {
        return get(outputVector)[index]
    }
}

internal class MutableOutputStateStorage(
    override val variableProvider: VariableProvider,
    override val data: MutableOutputStateMap,
) : OutputStateStorage, MutableOutputStateProvider {

    override fun copy(): OutputStateStorage = mutableCopy()

    override fun mutableCopy(): MutableOutputStateStorage {
        return MutableOutputStateStorage(variableProvider, data.toMutableMap())
    }

    override fun set(output: OutputScalar, value: LogicState) {
        // Check that an output with the given name exists.
        require(variableProvider.hasOutput(output)) { "Unknown output ${output.name}" }

        // Update the value of the output.
        data[output.name]!![0] = value
    }

    override fun set(outputVector: OutputVector, value: Array<LogicState>) {
        // Check that an output with the given name exists.
        require(variableProvider.hasOutput(outputVector)) { "Unknown output vector ${outputVector.name}" }

        // Update the value of the output.
        data[outputVector.name] = value
    }

    override fun set(outputVector: OutputVector, index: Int, value: LogicState) {
        // Check that an output with the given name exists.
        require(variableProvider.hasOutput(outputVector)) { "Unknown output vector ${outputVector.name}" }

        // Return the value of the output.
        data[outputVector.name]!![index] = value
    }

    fun update(data: OutputStateMap) {
        for ((outputName, value) in data) {
            // Check that only existing outputs are specified.
            val output = variableProvider.output(outputName)
                ?: throw IllegalArgumentException("Unknown output '$outputName'")

            // Update the value of the output.
            when (output) {
                is OutputScalar -> this[output] = value[0]
                is OutputVector -> this[output] = value
                is OutputVectorElement -> throw IllegalArgumentException("Output vector elements may not be specified ('$outputName')")
            }
        }
    }
}

internal fun outputStateStorage(variableProvider: VariableProvider, data: OutputStateMap): OutputStateStorage {
    return mutableOutputStateStorage(variableProvider, data)
}

internal fun outputStateStorage(variableProvider: VariableProvider, dataProvider: OutputStateProvider): OutputStateStorage {
    return mutableOutputStateStorage(variableProvider, dataProvider)
}

internal fun mutableOutputStateStorage(variableProvider: VariableProvider, data: OutputStateMap): MutableOutputStateStorage {
    val processedData: MutableOutputStateMap = mutableMapOf()
    for (output in variableProvider.outputs()) {
        // Check that the output has an assigned value.
        require(output.name in data) { "No value provided for the output ${output.name}" }

        // Get the value from the map.
        val value = data[output.name]!!

        // Put value into map.
        processedData[output.name] = value
    }
    return MutableOutputStateStorage(variableProvider, processedData)
}

internal fun mutableOutputStateStorage(variableProvider: VariableProvider, dataProvider: OutputStateProvider): MutableOutputStateStorage {
    val processedData: MutableOutputStateMap = mutableMapOf()
    for (output in variableProvider.outputs()) {
        // Check that the output has an assigned value.
        require(dataProvider.variableProvider.hasVariable(output)) { "No value provided for the output ${output.name}" }

        // Get the value from the provider.
        val value = when (output) {
            is OutputScalar -> arrayOf(dataProvider[output])
            is OutputVector -> dataProvider[output]
            else -> throw IllegalStateException()
        }

        // Put value into map.
        processedData[output.name] = value
    }
    return MutableOutputStateStorage(variableProvider, processedData)
}
