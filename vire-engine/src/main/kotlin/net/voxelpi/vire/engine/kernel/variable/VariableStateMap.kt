package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.Kernel

internal interface VectorVariableSizeMap : VectorVariableSizeProvider {

    val kernel: Kernel

    val vectorVariableSizes: Map<String, Int>

    override fun size(vector: VectorVariable<*>): Int = size(vector.name)

    override fun size(vectorName: String): Int {
        // Check that the vector variable is defined on the kernel.
        require(kernel.hasVectorVariable(vectorName))

        // Return the size of the vector variable from the map.
        return vectorVariableSizes[vectorName]!!
    }
}

internal interface MutableVectorVariableSizeMap : VectorVariableSizeMap, MutableVectorVariableSizeProvider {

    override val vectorVariableSizes: MutableMap<String, Int>

    override fun resize(vector: VectorVariable<*>, size: Int) = resize(vector.name, size)

    override fun resize(vectorName: String, size: Int) {
        // Check that the vector variable is defined on the kernel.
        require(kernel.hasVectorVariable(vectorName))

        // Check that the size of the vector variable is greater than 0.
        require(size >= 0) { "The size of a vector variable must be greater than or equal to zero" }

        // Modify the size of the vector variable in the map.
        vectorVariableSizes[vectorName] = size
    }
}

internal interface SettingStateMap : SettingStateProvider {

    val kernel: Kernel

    val variableStates: Map<String, Any?>

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(setting: Setting<T>): T {
        // Check that a setting with the given name exists.
        require(kernel.hasSetting(setting.name)) { "Unknown setting ${setting.name}" }

        // Return the value of the setting.
        return variableStates[setting.name] as T
    }
}

internal interface MutableSettingStateMap : SettingStateMap, MutableSettingStateProvider {

    override val variableStates: MutableMap<String, Any?>

    override fun <T> set(setting: Setting<T>, value: T) {
        // Check that a setting with the given name exists.
        require(kernel.hasParameter(setting.name)) { "Unknown setting ${setting.name}" }

        // Check that the value is valid for the specified setting.
        require(setting.isValidValue(value)) { "Value $setting does not meet the requirements for the setting ${setting.name}" }

        // Update the value of the parameter.
        variableStates[setting.name] = value
    }
}

internal interface FieldStateMap : FieldStateProvider {

    val kernel: Kernel

    val variableStates: Map<String, Any?>

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(field: Field<T>): T {
        // Check that a field with the given name exists.
        require(kernel.hasField(field.name)) { "Unknown field ${field.name}" }

        // Return the value of the field.
        return variableStates[field.name] as T
    }
}

internal interface MutableFieldStateMap : FieldStateMap, MutableFieldStateProvider {

    override val variableStates: MutableMap<String, Any?>

    override fun <T> set(field: Field<T>, value: T) {
        // Check that a field with the given name exists.
        require(kernel.hasParameter(field.name)) { "Unknown field ${field.name}" }

        // Check that the value is valid for the specified field.
        require(field.isValidValue(value)) { "Value $field does not meet the requirements for the field ${field.name}" }

        // Update the value of the parameter.
        variableStates[field.name] = value
    }
}

internal interface InputStateMap : InputStateProvider {

    val kernel: Kernel

    val variableStates: Map<String, Any?>

    override fun get(input: InputScalar): LogicState {
        // Check that an input with the given name exists.
        require(kernel.hasInput(input.name)) { "Unknown input ${input.name}" }

        // Return the value of the input.
        return variableStates[input.name] as LogicState
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(inputVector: InputVector): Array<LogicState> {
        // Check that an input with the given name exists.
        require(kernel.hasInput(inputVector.name)) { "Unknown input vector ${inputVector.name}" }

        // Return the value of the input.
        return variableStates[inputVector.name] as Array<LogicState>
    }

    override fun get(inputVector: InputVector, index: Int): LogicState {
        return get(inputVector)[index]
    }
}

internal interface MutableInputStateMap : InputStateMap, MutableInputStateProvider {

    override val variableStates: MutableMap<String, Any?>

    override fun set(input: InputScalar, value: LogicState) {
        // Check that an input with the given name exists.
        require(kernel.hasParameter(input.name)) { "Unknown input ${input.name}" }

        // Update the value of the parameter.
        variableStates[input.name] = value
    }

    override fun set(inputVector: InputVector, value: Array<LogicState>) {
        // Check that an input with the given name exists.
        require(kernel.hasParameter(inputVector.name)) { "Unknown input vector ${inputVector.name}" }

        // Update the value of the parameter.
        variableStates[inputVector.name] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(inputVector: InputVector, index: Int, value: LogicState) {
        // Check that an input with the given name exists.
        require(kernel.hasInput(inputVector.name)) { "Unknown input vector ${inputVector.name}" }

        // Return the value of the input.
        (variableStates[inputVector.name] as Array<LogicState>)[index]
    }
}

internal interface OutputStateMap : OutputStateProvider {

    val kernel: Kernel

    val variableStates: Map<String, Any?>

    override fun get(output: OutputScalar): LogicState {
        // Check that an output with the given name exists.
        require(kernel.hasOutput(output.name)) { "Unknown output ${output.name}" }

        // Return the value of the output.
        return variableStates[output.name] as LogicState
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(outputVector: OutputVector): Array<LogicState> {
        // Check that an output with the given name exists.
        require(kernel.hasOutput(outputVector.name)) { "Unknown output vector ${outputVector.name}" }

        // Return the value of the output.
        return variableStates[outputVector.name] as Array<LogicState>
    }

    override fun get(outputVector: OutputVector, index: Int): LogicState {
        return get(outputVector)[index]
    }
}

internal interface MutableOutputStateMap : OutputStateMap, MutableOutputStateProvider {

    override val variableStates: MutableMap<String, Any?>

    override fun set(output: OutputScalar, value: LogicState) {
        // Check that an output with the given name exists.
        require(kernel.hasParameter(output.name)) { "Unknown output ${output.name}" }

        // Update the value of the parameter.
        variableStates[output.name] = value
    }

    override fun set(outputVector: OutputVector, value: Array<LogicState>) {
        // Check that an output with the given name exists.
        require(kernel.hasParameter(outputVector.name)) { "Unknown output vector ${outputVector.name}" }

        // Update the value of the parameter.
        variableStates[outputVector.name] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(outputVector: OutputVector, index: Int, value: LogicState) {
        // Check that an output with the given name exists.
        require(kernel.hasOutput(outputVector.name)) { "Unknown output vector ${outputVector.name}" }

        // Return the value of the output.
        (variableStates[outputVector.name] as Array<LogicState>)[index]
    }
}
