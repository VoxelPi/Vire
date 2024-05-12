package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.BooleanState
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

/**
 * A type that provides ways to access the state of an input variable.
 */
public interface InputStateProvider {

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
}

/**
 * A type that provides ways to access and modify the state of an input variable.
 */
public interface MutableInputStateProvider : InputStateProvider {

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
}
