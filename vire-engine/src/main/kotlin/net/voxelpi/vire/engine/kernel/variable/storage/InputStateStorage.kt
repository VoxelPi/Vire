package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableInputStateProvider

public typealias InputStateMap = Map<String, Array<LogicState>>

public typealias MutableInputStateMap = MutableMap<String, Array<LogicState>>

/**
 * A collection that stores the state of all inputs of the given [variableProvider].
 */
public open class InputStateStorage(
    final override val variableProvider: VariableProvider,
    initialData: InputStateMap,
) : InputStateProvider {

    init {
        for ((inputName, inputState) in initialData) {
            val input = variableProvider.input(inputName)
                ?: throw IllegalStateException("Data specified for unknown input \"$inputName\".")

            for (channelState in inputState) {
                require(input.isValidValue(channelState)) { "Invalid value specified for input \"$inputName\"." }
            }
        }

        val missingVariables = variableProvider.inputs().map { it.name }.filter { it !in initialData }
        require(missingVariables.isEmpty()) {
            "Missing values for the following inputs: ${missingVariables.joinToString(", ") { "\"${it}\"" } }"
        }
    }

    protected open val data: InputStateMap = initialData.toMap()

    public constructor(variableProvider: VariableProvider, initialData: InputStateProvider) : this(
        variableProvider,
        variableProvider.inputs().filter { initialData.hasValue(it) }.associate { it.name to initialData.vector(it) }
    )

    /**
     * Creates a copy of this storage.
     */
    public fun copy(): InputStateStorage {
        return InputStateStorage(variableProvider, data)
    }

    /**
     * Creates a mutable copy of this storage.
     */
    public fun mutableCopy(): MutableInputStateStorage {
        return MutableInputStateStorage(variableProvider, data)
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
}

/**
 * A mutable collection that stores the state of all inputs of the given [variableProvider].
 */
public class MutableInputStateStorage(
    variableProvider: VariableProvider,
    initialData: InputStateMap,
) : InputStateStorage(variableProvider, initialData), MutableInputStateProvider {

    override val data: MutableInputStateMap = initialData.toMutableMap()

    public constructor(variableProvider: VariableProvider, initialData: InputStateProvider) : this(
        variableProvider,
        variableProvider.inputs().filter { initialData.hasValue(it) }.associate { it.name to initialData.vector(it) }
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
}
