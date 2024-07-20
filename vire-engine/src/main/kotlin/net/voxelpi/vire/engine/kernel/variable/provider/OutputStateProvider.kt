package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.BooleanState
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.Output
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.OutputVectorElement
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.patch.OutputStatePatch
import net.voxelpi.vire.engine.kernel.variable.storage.OutputStateMap

/**
 * A type that provides access to the state of some of the registered output variables.
 */
public interface PartialOutputStateProvider {

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

    /**
     * Returns the value of the given [output] as a logic state vector.
     * If the output is a vector, the vector value is returned directly.
     * If the output is a scalar, then an array with the value as its only entry is returned.
     *
     * @param output the output of which the value should be returned.
     */
    public fun vector(output: Output): Array<LogicState> {
        return when (output) {
            is OutputScalar -> arrayOf(this[output])
            is OutputVector -> this[output]
            is OutputVectorElement -> arrayOf(this[output])
        }
    }

    /**
     * Returns if the given output has a set value.
     */
    public fun hasValue(output: Output): Boolean

    /**
     * Checks if all registered outputs have a set value.
     */
    public fun allOutputsSet(): Boolean
}

/**
 * A type that provides mutable access to the state of some of the registered output variables.
 */
public interface MutablePartialOutputStateProvider : PartialOutputStateProvider {

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

    /**
     * Sets the value of the given [output] to the given [value].
     *
     * @param output the output of which the value should be modified.
     * @param value the new value of the output.
     */
    public operator fun set(output: OutputScalar, value: BooleanState) {
        set(output, value.logicState())
    }

    /**
     * Sets the value of all entries of the given [outputVector] to the given [value].
     *
     * @param outputVector the output vector of which the value should be modified.
     * @param value the new value of the output.
     */
    public operator fun set(outputVector: OutputVector, value: Array<BooleanState>) {
        set(outputVector, value.map { it.logicState() }.toTypedArray())
    }

    /**
     * Sets the value of the entry at the given [index] of the given [outputVector] to the given [value].
     *
     * @param outputVector the output vector of which the value should be modified.
     * @param index the index in the output vector of the entry.
     * @param value the new value of the output.
     */
    public operator fun set(outputVector: OutputVector, index: Int, value: BooleanState) {
        set(outputVector, index, value.logicState())
    }

    /**
     * Sets the value of the given [outputVectorElement] to the given [value].
     *
     * @param outputVectorElement the output vector element of which the value should be modified.
     * @param value the new value of the output.
     */
    public operator fun set(outputVectorElement: OutputVectorElement, value: BooleanState) {
        set(outputVectorElement.vector, outputVectorElement.index, value)
    }

    /**
     * Sets the value of the given [output] to the given logic state vector [value].
     * If the output is a vector, the vector value is used directly.
     * If the output is a scalar, then the first element of the array is used.
     *
     * @param output the output of which the value should be modified.
     * @param value the value that should be used.
     */
    public fun vector(output: Output, value: Array<LogicState>) {
        when (output) {
            is OutputScalar -> this[output] = value[0]
            is OutputVector -> this[output] = value
            is OutputVectorElement -> this[output] = value[0]
        }
    }

    /**
     * Copies all values present in the given [provider] to this provider.
     */
    public fun applyOutputStatePatch(provider: PartialOutputStateProvider) {
        for (output in provider.variableProvider.outputs().filter(provider::hasValue)) {
            vector(output, provider.vector(output))
        }
    }

    /**
     * Copies all values present in the given [map] to this provider.
     */
    public fun applyOutputStatePatch(map: OutputStateMap) {
        applyOutputStatePatch(OutputStatePatch(variableProvider, map))
    }
}

/**
 * A type that provides access to the state of all registered output variables.
 */
public interface OutputStateProvider : PartialOutputStateProvider {

    /**
     * Returns the value of the given [output].
     *
     * @param output the variable of which the value should be returned.
     */
    override fun get(output: OutputScalar): LogicState

    /**
     * Returns the value of all entries of the given [outputVector].
     *
     * @param outputVector the output vector of which the value should be returned.
     */
    override fun get(outputVector: OutputVector): Array<LogicState>

    /**
     * Returns the value of the entry at the given [index] of the given [outputVector].
     *
     * @param outputVector the output vector of which the value should be returned.
     * @param index the index in the output vector of the entry.
     */
    override fun get(outputVector: OutputVector, index: Int): LogicState

    /**
     * Returns the value of the given [outputVectorElement].
     *
     * @param outputVectorElement the output vector element of which the value should be returned.
     */
    override fun get(outputVectorElement: OutputVectorElement): LogicState {
        return get(outputVectorElement.vector, outputVectorElement.index)
    }

    /**
     * Returns the value of the given [output] as a logic state vector.
     * If the output is a vector, the vector value is returned directly.
     * If the output is a scalar, then an array with the value as its only entry is returned.
     *
     * @param output the output of which the value should be returned.
     */
    override fun vector(output: Output): Array<LogicState> {
        return when (output) {
            is OutputScalar -> arrayOf(this[output])
            is OutputVector -> this[output]
            is OutputVectorElement -> arrayOf(this[output])
        }
    }

    override fun hasValue(output: Output): Boolean = output in variableProvider.outputs()

    override fun allOutputsSet(): Boolean = true
}

/**
 * A type that provides mutable access to the state of all registered output variables.
 */
public interface MutableOutputStateProvider : OutputStateProvider, MutablePartialOutputStateProvider
