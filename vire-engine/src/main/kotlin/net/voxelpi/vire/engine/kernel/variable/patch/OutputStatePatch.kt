package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.Output
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.UninitializedVariableException
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.MutableOutputStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.OutputStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.OutputStateStorage

/**
 * A collection that stores the state of all outputs of the given [variableProvider].
 */
public open class OutputStatePatch(
    final override val variableProvider: VariableProvider,
    initialData: OutputStateMap = emptyMap(),
) : PartialOutputStateProvider {

    init {
        for ((outputName, outputState) in initialData) {
            val output = variableProvider.output(outputName)
                ?: throw IllegalStateException("Data specified for unknown output \"$outputName\".")

            for (channelState in outputState) {
                require(output.isValidValue(channelState)) { "Invalid value specified for output \"$outputName\"." }
            }
        }
    }

    protected open val data: OutputStateMap = initialData.toMap()

    public constructor(variableProvider: VariableProvider, initialData: PartialOutputStateProvider) : this(
        variableProvider,
        variableProvider.outputs().filter { initialData.hasValue(it) }.associate { it.name to initialData.vector(it)!! }
    )

    /**
     * Creates a copy of this patch.
     */
    public fun copy(): OutputStatePatch {
        return OutputStatePatch(variableProvider, data)
    }

    /**
     * Creates a mutable copy of this patch.
     */
    public fun mutableCopy(): MutableOutputStatePatch {
        return MutableOutputStatePatch(variableProvider, data)
    }

    override fun get(output: OutputScalar): LogicState {
        // Check that an output with the given name exists.
        require(variableProvider.hasOutput(output)) { "Unknown output ${output.name}" }

        // Check that the output has been initialized.
        if (output.name !in data) {
            throw UninitializedVariableException(output)
        }

        // Return the value of the output.
        return data[output.name]!![0]
    }

    override fun get(outputVector: OutputVector): Array<LogicState> {
        // Check that an output with the given name exists.
        require(variableProvider.hasOutput(outputVector)) { "Unknown output vector ${outputVector.name}" }

        // Check that the output vector has been initialized.
        if (outputVector.name !in data) {
            throw UninitializedVariableException(outputVector)
        }

        // Return the value of the output.
        return data[outputVector.name]!!
    }

    override fun get(outputVector: OutputVector, index: Int): LogicState {
        return get(outputVector)[index]
    }

    override fun hasValue(output: Output): Boolean {
        return output.name in data
    }

    override fun allOutputsSet(): Boolean {
        return variableProvider.outputs().all { hasValue(it) }
    }

    /**
     * Creates an output state storage using the set data.
     * All outputs must have a set value otherwise this operation fails.
     */
    public fun createStorage(): OutputStateStorage {
        return OutputStateStorage(variableProvider, data)
    }
}

/**
 * A mutable collection that stores the state of some outputs of the given [variableProvider].
 */
public class MutableOutputStatePatch(
    variableProvider: VariableProvider,
    initialData: OutputStateMap = emptyMap(),
) : OutputStatePatch(variableProvider, initialData), MutablePartialOutputStateProvider {

    override val data: MutableOutputStateMap = initialData.toMutableMap()

    public constructor(variableProvider: VariableProvider, initialData: PartialOutputStateProvider) : this(
        variableProvider,
        variableProvider.outputs().filter { initialData.hasValue(it) }.associate { it.name to initialData.vector(it) }
    )

    override fun set(output: OutputScalar, value: LogicState) {
        // Check that an output with the given name exists.
        require(variableProvider.hasOutput(output)) { "Unknown output ${output.name}" }

        // Update the value of the output.
        data[output.name] = arrayOf(value)
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
}
