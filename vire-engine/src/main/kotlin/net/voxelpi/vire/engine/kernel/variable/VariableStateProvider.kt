package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.LogicState

/**
 * A type that provides ways to access the size of an input variable.
 */
public interface InputSizeProvider {

    /**
     * Returns the size of the given [input].
     */
    public fun size(input: Input): Int
}

/**
 * A type that provides ways to access and modify the size of an input variable.
 */
public interface MutableInputSizeProvider {

    /**
     * Changes the size of the given [input] to the given [size].
     */
    public fun resize(input: Input, size: Int)
}

/**
 * A type that provides ways to access the size of an output variable.
 */
public interface OutputSizeProvider {

    /**
     * Returns the size of the given [output].
     */
    public fun size(output: Output): Int
}

/**
 * A type that provides ways to access and modify the size of an output variable.
 */
public interface MutableOutputSizeProvider {

    /**
     * Changes the size of the given [output] to the given [size].
     */
    public fun resize(output: Output, size: Int)
}

/**
 * A type that provides ways to access the state of a parameter variable.
 */
public interface ParameterStateProvider {

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
public interface InputStateProvider : InputSizeProvider {

    /**
     * Returns the current value of the given [input].
     *
     * @param input the variable of which the value should be returned.
     */
    public operator fun get(input: Input): Array<LogicState>
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
    public operator fun set(input: Input, value: Array<LogicState>)
}

/**
 * A type that provides ways to access the state of an output variable.
 */
public interface OutputStateProvider : OutputSizeProvider {

    /**
     * Returns the current value of the given [output].
     *
     * @param output the variable of which the value should be returned.
     */
    public operator fun get(output: Output): Array<LogicState>
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
    public operator fun set(output: Output, value: Array<LogicState>)
}

public interface IOVectorSizeProvider : InputSizeProvider, OutputSizeProvider

public interface MutableIOVectorSizeProvider : MutableInputSizeProvider, MutableOutputSizeProvider

public interface IOVectorStateProvider : InputStateProvider, OutputStateProvider

public interface MutableIOVectorStateProvider : MutableInputStateProvider, MutableOutputStateProvider
