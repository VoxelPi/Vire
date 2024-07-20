package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.BooleanState
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.Input
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

/**
 * A type that provides access to the state of some of the registered input variables.
 */
public interface PartialInputStateProvider {

    /**
     * The variable provider for which the input states should be provided.
     */
    public val variableProvider: VariableProvider

    /**
     * Returns the value of the given [input].
     *
     * @param input the input of which the value should be returned.
     */
    public operator fun get(input: InputScalar): LogicState

    /**
     * Returns the value of all entries of the given [inputVector].
     *
     * @param inputVector the input vector of which the value should be returned.
     */
    public operator fun get(inputVector: InputVector): Array<LogicState>

    /**
     * Returns the value of the entry at the given [index] of the given [inputVector].
     *
     * @param inputVector the input vector of which the value should be returned.
     * @param index the index in the input vector of the entry.
     */
    public operator fun get(inputVector: InputVector, index: Int): LogicState

    /**
     * Returns the value of the given [inputVectorElement].
     *
     * @param inputVectorElement the input vector element of which the value should be returned.
     */
    public operator fun get(inputVectorElement: InputVectorElement): LogicState {
        return get(inputVectorElement.vector, inputVectorElement.index)
    }

    /**
     * Returns the value of the given [input] as a logic state vector.
     * If the input is a vector, the vector value is returned directly.
     * If the input is a scalar, then an array with the value as its only entry is returned.
     *
     * @param input the input of which the value should be returned.
     */
    public fun vector(input: Input): Array<LogicState> {
        return when (input) {
            is InputScalar -> arrayOf(this[input])
            is InputVector -> this[input]
            is InputVectorElement -> arrayOf(this[input])
        }
    }

    /**
     * Returns if the given input has a set value.
     */
    public fun hasValue(input: Input): Boolean

    /**
     * Checks if all registered inputs have a set value.
     */
    public fun allInputsSet(): Boolean
}

/**
 * A type that provides mutable access to the state of some of the registered input variables.
 */
public interface MutablePartialInputStateProvider : PartialInputStateProvider {

    /**
     * Sets the value of the given [input] to the given [value].
     *
     * @param input the input of which the value should be modified.
     * @param value the new value of the input.
     */
    public operator fun set(input: InputScalar, value: LogicState)

    /**
     * Sets the value of all entries of the given [inputVector] to the given [value].
     *
     * @param inputVector the input vector of which the value should be modified.
     * @param value the new value of the input.
     */
    public operator fun set(inputVector: InputVector, value: Array<LogicState>)

    /**
     * Sets the value of the entry at the given [index] of the given [inputVector] to the given [value].
     *
     * @param inputVector the input vector of which the value should be modified.
     * @param index the index in the input vector of the entry.
     * @param value the new value of the input.
     */
    public operator fun set(inputVector: InputVector, index: Int, value: LogicState)

    /**
     * Sets the value of the given [inputVectorElement] to the given [value].
     *
     * @param inputVectorElement the input vector element of which the value should be modified.
     * @param value the new value of the input.
     */
    public operator fun set(inputVectorElement: InputVectorElement, value: LogicState) {
        set(inputVectorElement.vector, inputVectorElement.index, value)
    }

    /**
     * Sets the value of the given [input] to the given [value].
     *
     * @param input the input of which the value should be modified.
     * @param value the new value of the input.
     */
    public operator fun set(input: InputScalar, value: BooleanState) {
        set(input, value.logicState())
    }

    /**
     * Sets the value of all entries of the given [inputVector] to the given [value].
     *
     * @param inputVector the input vector of which the value should be modified.
     * @param value the new value of the input.
     */
    public operator fun set(inputVector: InputVector, value: Array<BooleanState>) {
        set(inputVector, value.map { it.logicState() }.toTypedArray())
    }

    /**
     * Sets the value of the entry at the given [index] of the given [inputVector] to the given [value].
     *
     * @param inputVector the input vector of which the value should be modified.
     * @param index the index in the input vector of the entry.
     * @param value the new value of the input.
     */
    public operator fun set(inputVector: InputVector, index: Int, value: BooleanState) {
        set(inputVector, index, value.logicState())
    }

    /**
     * Sets the value of the given [inputVectorElement] to the given [value].
     *
     * @param inputVectorElement the input vector element of which the value should be modified.
     * @param value the new value of the input.
     */
    public operator fun set(inputVectorElement: InputVectorElement, value: BooleanState) {
        set(inputVectorElement.vector, inputVectorElement.index, value)
    }

    /**
     * Sets the value of the given [input] to the given logic state vector [value].
     * If the input is a vector, the vector value is used directly.
     * If the input is a scalar, then the first element of the array is used.
     *
     * @param input the input of which the value should be modified.
     * @param value the value that should be used.
     */
    public fun vector(input: Input, value: Array<LogicState>) {
        when (input) {
            is InputScalar -> this[input] = value[0]
            is InputVector -> this[input] = value
            is InputVectorElement -> this[input] = value[0]
        }
    }
}

/**
 * A type that provides access to the state of all registered input variables.
 */
public interface InputStateProvider : PartialInputStateProvider {

    /**
     * Returns the value of the given [input].
     *
     * @param input the input of which the value should be returned.
     */
    override fun get(input: InputScalar): LogicState

    /**
     * Returns the value of all entries of the given [inputVector].
     *
     * @param inputVector the input vector of which the value should be returned.
     */
    override fun get(inputVector: InputVector): Array<LogicState>

    /**
     * Returns the value of the entry at the given [index] of the given [inputVector].
     *
     * @param inputVector the input vector of which the value should be returned.
     * @param index the index in the input vector of the entry.
     */
    override fun get(inputVector: InputVector, index: Int): LogicState

    /**
     * Returns the value of the given [inputVectorElement].
     *
     * @param inputVectorElement the input vector element of which the value should be returned.
     */
    override fun get(inputVectorElement: InputVectorElement): LogicState {
        return get(inputVectorElement.vector, inputVectorElement.index)
    }

    /**
     * Returns the value of the given [input] as a logic state vector.
     * If the input is a vector, the vector value is returned directly.
     * If the input is a scalar, then an array with the value as its only entry is returned.
     *
     * @param input the input of which the value should be returned.
     */
    override fun vector(input: Input): Array<LogicState> {
        return when (input) {
            is InputScalar -> arrayOf(this[input])
            is InputVector -> this[input]
            is InputVectorElement -> arrayOf(this[input])
        }
    }

    override fun hasValue(input: Input): Boolean = input in variableProvider.inputs()

    override fun allInputsSet(): Boolean = true
}

/**
 * A type that provides mutable access to the state of all registered input variables.
 */
public interface MutableInputStateProvider : InputStateProvider, MutablePartialInputStateProvider
