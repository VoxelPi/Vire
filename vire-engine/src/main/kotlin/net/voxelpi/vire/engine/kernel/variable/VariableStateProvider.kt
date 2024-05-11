package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.LogicState

/**
 * A type that provides ways to access the size of a vector variable.
 */
public interface VectorSizeProvider {

    /**
     * The variable provider for which the vector sizes should be provided.
     */
    public val variableProvider: VariableProvider

    /**
     * Returns the size of the given [vector].
     */
    public fun size(vector: VectorVariable<*>): Int

    /**
     * Returns the size of the vector with the given [vectorName].
     */
    public fun size(vectorName: String): Int
}

/**
 * A type that provides ways to access and modify the size of a vector variable.
 */
public interface MutableVectorSizeProvider : VectorSizeProvider {

    /**
     * Changes the size of the given [vector] to the given [size].
     */
    public fun resize(vector: VectorVariable<*>, size: Int)

    /**
     * Changes the size of the vector with the given [vectorName] to the given [size].
     */
    public fun resize(vectorName: String, size: Int)
}

/**
 * A type that provides ways to access the state of a parameter variable.
 */
public interface ParameterStateProvider {

    /**
     * The parameter provider for which the parameter states should be provided.
     */
    public val parameterProvider: ParameterProvider

    /**
     * Returns the current value of the given [parameter].
     *
     * @param parameter the parameter of which the value should be returned.
     */
    public operator fun <T> get(parameter: Parameter<T>): T
}

/**
 * A type that provides ways to access and modify the state of a parameter variable.
 */
public interface MutableParameterStateProvider : ParameterStateProvider {

    /**
     * Sets the value of the given [parameter] to the given [value].
     *
     * @param parameter the parameter of which the value should be modified.
     * @param value the new value of the parameter.
     */
    public operator fun <T> set(parameter: Parameter<T>, value: T)
}

/**
 * A type that provides ways to access the state of a setting variable.
 */
public interface SettingStateProvider {

    /**
     * The setting provider for which the setting states should be provided.
     */
    public val settingProvider: SettingProvider

    /**
     * Returns the current value of the given [setting].
     *
     * @param setting the variable of which the value should be returned.
     */
    public operator fun <T> get(setting: Setting<T>): T
}

/**
 * A type that provides ways to access and modify the state of a setting variable.
 */
public interface MutableSettingStateProvider : SettingStateProvider {

    /**
     * Sets the value of the given [setting] to the given [value].
     *
     * @param setting the setting of which the value should be modified.
     * @param value the new value of the setting.
     */
    public operator fun <T> set(setting: Setting<T>, value: T)
}

/**
 * A type that provides ways to access the state of a field variable.
 */
public interface FieldStateProvider {

    /**
     * The field provider for which the field states should be provided.
     */
    public val fieldProvider: FieldProvider

    /**
     * Returns the current value of the given [field].
     *
     * @param field the variable of which the value should be returned.
     */
    public operator fun <T> get(field: Field<T>): T
}

/**
 * A type that provides ways to access and modify the state of a field variable.
 */
public interface MutableFieldStateProvider : FieldStateProvider {

    /**
     * Sets the value of the given [field] to the given [value].
     *
     * @param field the field of which the value should be modified.
     * @param value the new value of the field.
     */
    public operator fun <T> set(field: Field<T>, value: T)
}

/**
 * A type that provides ways to access the state of an input variable.
 */
public interface InputStateProvider {

    /**
     * The input provider for which the input states should be provided.
     */
    public val inputProvider: InputProvider

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
}

/**
 * A type that provides ways to access the state of an output variable.
 */
public interface OutputStateProvider {

    /**
     * The output provider for which the output states should be provided.
     */
    public val outputProvider: OutputProvider

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

public interface IOVectorStateProvider : InputStateProvider, OutputStateProvider

public interface MutableIOVectorStateProvider : MutableInputStateProvider, MutableOutputStateProvider
