package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.OutputVectorElement
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

/**
 * A type that provides ways to access the state of an output variable.
 */
public interface OutputStateProvider {

    /**
     * The variable provider for which the output states should be provided.
     */
    public val variableProvider: VariableProvider

    /**
     * Returns the value of the given [output].
     *
     * @param output the variable of which the value should be returned.
     */
    public operator fun get(output: OutputScalar): LogicState

    /**
     * Returns the value of all entries of the given [outputVector].
     *
     * @param outputVector the output vector of which the value should be returned.
     */
    public operator fun get(outputVector: OutputVector): Array<LogicState>

    /**
     * Returns the value of the entry at the given [index] of the given [outputVector].
     *
     * @param outputVector the output vector of which the value should be returned.
     * @param index the index in the output vector of the entry.
     */
    public operator fun get(outputVector: OutputVector, index: Int): LogicState

    /**
     * Returns the value of the given [outputVectorElement].
     *
     * @param outputVectorElement the output vector element of which the value should be returned.
     */
    public operator fun get(outputVectorElement: OutputVectorElement): LogicState {
        return get(outputVectorElement.vector, outputVectorElement.index)
    }
}

/**
 * A type that provides ways to access and modify the state of an output variable.
 */
public interface MutableOutputStateProvider : OutputStateProvider {

    /**
     * Sets the value of the given [output] to the given [value].
     *
     * @param output the output of which the value should be modified.
     * @param value the new value of the output.
     */
    public operator fun set(output: OutputScalar, value: LogicState)

    /**
     * Sets the value of all entries of the given [outputVector] to the given [value].
     *
     * @param outputVector the output vector of which the value should be modified.
     * @param value the new value of the output.
     */
    public operator fun set(outputVector: OutputVector, value: Array<LogicState>)

    /**
     * Sets the value of the entry at the given [index] of the given [outputVector] to the given [value].
     *
     * @param outputVector the output vector of which the value should be modified.
     * @param index the index in the output vector of the entry.
     * @param value the new value of the output.
     */
    public operator fun set(outputVector: OutputVector, index: Int, value: LogicState)

    /**
     * Sets the value of the given [outputVectorElement] to the given [value].
     *
     * @param outputVectorElement the output vector element of which the value should be modified.
     * @param value the new value of the output.
     */
    public operator fun set(outputVectorElement: OutputVectorElement, value: LogicState) {
        set(outputVectorElement.vector, outputVectorElement.index, value)
    }
}
