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

internal open class OutputStateStorage(
    final override val variableProvider: VariableProvider,
    initialData: OutputStateMap,
) : OutputStateProvider {

    init {
        for ((outputName, outputState) in initialData) {
            val output = variableProvider.output(outputName)
                ?: throw IllegalStateException("Data specified for unknown output \"$outputName\".")

            for (channelState in outputState) {
                require(output.isValidValue(channelState)) { "Invalid value specified for output \"$outputName\"." }
            }
        }

        val missingVariables = variableProvider.outputs().map { it.name }.filter { it !in initialData }
        require(missingVariables.isEmpty()) {
            "Missing values for the following outputs: ${missingVariables.joinToString(", ") { "\"${it}\"" } }"
        }
    }

    protected open val data: OutputStateMap = initialData.toMap()

    constructor(variableProvider: VariableProvider, initialData: OutputStateProvider) : this(
        variableProvider,
        variableProvider.outputs().filter { initialData.hasValue(it) }.associate { it.name to initialData.vector(it) }
    )

    fun copy(): OutputStateStorage {
        return OutputStateStorage(variableProvider, data)
    }

    fun mutableCopy(): MutableOutputStateStorage {
        return MutableOutputStateStorage(variableProvider, data)
    }

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
    variableProvider: VariableProvider,
    initialData: OutputStateMap,
) : OutputStateStorage(variableProvider, initialData), MutableOutputStateProvider {

    override val data: MutableOutputStateMap = initialData.toMutableMap()

    constructor(variableProvider: VariableProvider, initialData: OutputStateProvider) : this(
        variableProvider,
        variableProvider.outputs().filter { initialData.hasValue(it) }.associate { it.name to initialData.vector(it) }
    )

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
