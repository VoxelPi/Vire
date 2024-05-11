package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.LogicState

internal interface VectorSizeStorage : VectorSizeProvider {

    override val variableProvider: VariableProvider

    val data: VectorSizeMap

    fun copy(): VectorSizeStorage

    fun mutableCopy(): MutableVectorSizeStorage

    override fun size(vector: VectorVariable<*>): Int = size(vector.name)

    override fun size(vectorName: String): Int {
        // Check that the vector variable is defined on the kernel.
        require(variableProvider.hasVectorVariable(vectorName))

        // Return the size of the vector variable from the map.
        return data[vectorName]!!
    }
}

internal class MutableVectorSizeStorage(
    override val variableProvider: VariableProvider,
    override val data: MutableVectorSizeMap,
) : VectorSizeStorage, MutableVectorSizeProvider {

    override fun copy(): VectorSizeStorage = mutableCopy()

    override fun mutableCopy(): MutableVectorSizeStorage {
        return MutableVectorSizeStorage(variableProvider, data.toMutableMap())
    }

    override fun resize(vector: VectorVariable<*>, size: Int) = resize(vector.name, size)

    override fun resize(vectorName: String, size: Int) {
        // Check that the vector variable is defined on the kernel.
        require(variableProvider.hasVectorVariable(vectorName))

        // Check that the size of the vector variable is greater than 0.
        require(size >= 0) { "The size of a vector variable must be greater than or equal to zero" }

        // Modify the size of the vector variable in the map.
        data[vectorName] = size
    }
}

internal fun vectorSizeStorage(variableProvider: VariableProvider, data: VectorSizeMap): VectorSizeStorage {
    return mutableVectorSizeStorage(variableProvider, data)
}

internal fun vectorSizeStorage(variableProvider: VariableProvider, dataProvider: VectorSizeProvider): VectorSizeStorage {
    return mutableVectorSizeStorage(variableProvider, dataProvider)
}

internal fun mutableVectorSizeStorage(variableProvider: VariableProvider, data: VectorSizeMap): MutableVectorSizeStorage {
    val processedData: MutableVectorSizeMap = mutableMapOf()
    for (vector in variableProvider.vectorVariables()) {
        // Check that the parameter has an assigned value.
        require(vector.name in data) { "No size provided for the vector ${vector.name}" }

        // Get the size from the map.
        val size = data[vector.name]!!

        // Put value into map.
        processedData[vector.name] = size
    }
    return MutableVectorSizeStorage(variableProvider, processedData)
}

internal fun mutableVectorSizeStorage(variableProvider: VariableProvider, dataProvider: VectorSizeProvider): MutableVectorSizeStorage {
    val processedData: MutableVectorSizeMap = mutableMapOf()
    for (vector in variableProvider.vectorVariables()) {
        // Check that the parameter has an assigned value.
        require(dataProvider.variableProvider.hasVariable(vector)) { "No size provided for the vector ${vector.name}" }

        // Get the size from the provider.
        val size = dataProvider.size(vector.name)

        // Put value into map.
        processedData[vector.name] = size
    }
    return MutableVectorSizeStorage(variableProvider, processedData)
}

internal interface ParameterStateStorage : ParameterStateProvider {

    override val parameterProvider: ParameterProvider

    val data: ParameterStateMap

    fun copy(): ParameterStateStorage

    fun mutableCopy(): MutableParameterStateStorage

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameter: Parameter<T>): T {
        // Check that a parameter with the given name exists.
        require(parameterProvider.hasParameter(parameter.name)) { "Unknown parameter ${parameter.name}" }

        // Return the value of the parameter.
        return data[parameter.name] as T
    }
}

internal class MutableParameterStateStorage(
    override val parameterProvider: ParameterProvider,
    override val data: MutableParameterStateMap,
) : ParameterStateStorage, MutableParameterStateProvider {

    override fun copy(): MutableParameterStateStorage = mutableCopy()

    override fun mutableCopy(): MutableParameterStateStorage {
        return MutableParameterStateStorage(parameterProvider, data.toMutableMap())
    }

    override fun <T> set(parameter: Parameter<T>, value: T) {
        // Check that a parameter with the given name exists.
        require(parameterProvider.hasParameter(parameter.name)) { "Unknown parameter ${parameter.name}" }

        // Check that the value is valid for the specified parameter.
        require(parameter.isValidValue(value)) { "Value $parameter does not meet the requirements for the parameter ${parameter.name}" }

        // Update the value of the parameter.
        data[parameter.name] = value
    }
}

internal fun parameterStateStorage(variableProvider: ParameterProvider, data: ParameterStateMap): ParameterStateStorage {
    return mutableParameterStateStorage(variableProvider, data)
}

internal fun parameterStateStorage(variableProvider: ParameterProvider, dataProvider: ParameterStateProvider): ParameterStateStorage {
    return mutableParameterStateStorage(variableProvider, dataProvider)
}

internal fun mutableParameterStateStorage(variableProvider: ParameterProvider, data: ParameterStateMap): MutableParameterStateStorage {
    val processedData: MutableParameterStateMap = mutableMapOf()
    for (parameter in variableProvider.parameters()) {
        // Check that the parameter has an assigned value.
        require(parameter.name in data) { "No value provided for the parameter ${parameter.name}" }

        // Get the value from the map.
        val value = data[parameter.name]

        // Check that the assigned value is valid for the given parameter.
        require(parameter.isValidTypeAndValue(value)) { "Invalid value for the parameter ${parameter.name}" }

        // Put value into map.
        processedData[parameter.name] = value
    }
    return MutableParameterStateStorage(variableProvider, processedData)
}

internal fun mutableParameterStateStorage(
    variableProvider: ParameterProvider,
    dataProvider: ParameterStateProvider,
): MutableParameterStateStorage {
    val processedData: MutableParameterStateMap = mutableMapOf()
    for (parameter in variableProvider.parameters()) {
        // Check that the parameter has an assigned value.
        require(dataProvider.parameterProvider.hasVariable(parameter)) { "No value provided for the parameter ${parameter.name}" }

        // Get the value from the provider.
        val value = dataProvider[parameter]

        // Check that the assigned value is valid for the given parameter.
        require(parameter.isValidTypeAndValue(value)) { "Invalid value for the parameter ${parameter.name}" }

        // Put value into map.
        processedData[parameter.name] = value
    }
    return MutableParameterStateStorage(variableProvider, processedData)
}

internal interface SettingStateStorage : SettingStateProvider {

    override val settingProvider: SettingProvider

    val data: SettingStateMap

    fun copy(): SettingStateStorage

    fun mutableCopy(): MutableSettingStateStorage

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(setting: Setting<T>): T {
        // Check that a setting with the given name exists.
        require(settingProvider.hasSetting(setting.name)) { "Unknown setting ${setting.name}" }

        // Return the value of the setting.
        return data[setting.name] as T
    }
}

internal class MutableSettingStateStorage(
    override val settingProvider: SettingProvider,
    override val data: MutableSettingStateMap,
) : SettingStateStorage, MutableSettingStateProvider {

    override fun copy(): MutableSettingStateStorage = mutableCopy()

    override fun mutableCopy(): MutableSettingStateStorage {
        return MutableSettingStateStorage(settingProvider, data.toMutableMap())
    }

    override fun <T> set(setting: Setting<T>, value: T) {
        // Check that a setting with the given name exists.
        require(settingProvider.hasSetting(setting.name)) { "Unknown setting ${setting.name}" }

        // Check that the value is valid for the specified setting.
        require(setting.isValidValue(value)) { "Value $setting does not meet the requirements for the setting ${setting.name}" }

        // Update the value of the setting.
        data[setting.name] = value
    }
}

internal fun settingStateStorage(variableProvider: SettingProvider, data: SettingStateMap): SettingStateStorage {
    return mutableSettingStateStorage(variableProvider, data)
}

internal fun settingStateStorage(variableProvider: SettingProvider, dataProvider: SettingStateProvider): SettingStateStorage {
    return mutableSettingStateStorage(variableProvider, dataProvider)
}

internal fun mutableSettingStateStorage(variableProvider: SettingProvider, data: SettingStateMap): MutableSettingStateStorage {
    val processedData: MutableSettingStateMap = mutableMapOf()
    for (setting in variableProvider.settings()) {
        // Check that the setting has an assigned value.
        require(setting.name in data) { "No value provided for the setting ${setting.name}" }

        // Get the value from the map.
        val value = data[setting.name]

        // Check that the assigned value is valid for the given setting.
        require(setting.isValidTypeAndValue(value)) { "Invalid value for the setting ${setting.name}" }

        // Put value into map.
        processedData[setting.name] = value
    }
    return MutableSettingStateStorage(variableProvider, processedData)
}

internal fun mutableSettingStateStorage(variableProvider: SettingProvider, dataProvider: SettingStateProvider): MutableSettingStateStorage {
    val processedData: MutableSettingStateMap = mutableMapOf()
    for (setting in variableProvider.settings()) {
        // Check that the setting has an assigned value.
        require(dataProvider.settingProvider.hasVariable(setting)) { "No value provided for the setting ${setting.name}" }

        // Get the value from the provider.
        val value = dataProvider[setting]

        // Check that the assigned value is valid for the given setting.
        require(setting.isValidTypeAndValue(value)) { "Invalid value for the setting ${setting.name}" }

        // Put value into map.
        processedData[setting.name] = value
    }
    return MutableSettingStateStorage(variableProvider, processedData)
}

internal interface FieldStateStorage : FieldStateProvider {

    override val fieldProvider: FieldProvider

    val data: FieldStateMap

    fun copy(): FieldStateStorage

    fun mutableCopy(): MutableFieldStateStorage

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(field: Field<T>): T {
        // Check that a field with the given name exists.
        require(fieldProvider.hasField(field.name)) { "Unknown field ${field.name}" }

        // Return the value of the field.
        return data[field.name] as T
    }
}

internal class MutableFieldStateStorage(
    override val fieldProvider: FieldProvider,
    override val data: MutableFieldStateMap,
) : FieldStateStorage, MutableFieldStateProvider {

    override fun copy(): MutableFieldStateStorage = mutableCopy()

    override fun mutableCopy(): MutableFieldStateStorage {
        return MutableFieldStateStorage(fieldProvider, data.toMutableMap())
    }

    override fun <T> set(field: Field<T>, value: T) {
        // Check that a field with the given name exists.
        require(fieldProvider.hasField(field.name)) { "Unknown field ${field.name}" }

        // Check that the value is valid for the specified field.
        require(field.isValidTypeAndValue(value)) { "Value $field does not meet the requirements for the field ${field.name}" }

        // Update the value of the field.
        data[field.name] = value
    }
}

internal fun fieldStateStorage(variableProvider: FieldProvider, data: FieldStateMap): FieldStateStorage {
    return mutableFieldStateStorage(variableProvider, data)
}

internal fun fieldStateStorage(variableProvider: FieldProvider, dataProvider: FieldStateProvider): FieldStateStorage {
    return mutableFieldStateStorage(variableProvider, dataProvider)
}

internal fun mutableFieldStateStorage(variableProvider: FieldProvider, data: FieldStateMap): MutableFieldStateStorage {
    val processedData: MutableFieldStateMap = mutableMapOf()
    for (field in variableProvider.fields()) {
        // Check that the field has an assigned value.
        require(field.name in data) { "No value provided for the field ${field.name}" }

        // Get the value from the map.
        val value = data[field.name]

        // Check that the assigned value is valid for the given field.
        require(field.isValidTypeAndValue(value)) { "Invalid value for the field ${field.name}" }

        // Put value into map.
        processedData[field.name] = value
    }
    return MutableFieldStateStorage(variableProvider, processedData)
}

internal fun mutableFieldStateStorage(variableProvider: FieldProvider, dataProvider: FieldStateProvider): MutableFieldStateStorage {
    val processedData: MutableFieldStateMap = mutableMapOf()
    for (field in variableProvider.fields()) {
        // Check that the field has an assigned value.
        require(dataProvider.fieldProvider.hasVariable(field)) { "No value provided for the field ${field.name}" }

        // Get the value from the provider.
        val value = dataProvider[field]

        // Check that the assigned value is valid for the given field.
        require(field.isValidTypeAndValue(value)) { "Invalid value for the field ${field.name}" }

        // Put value into map.
        processedData[field.name] = value
    }
    return MutableFieldStateStorage(variableProvider, processedData)
}

internal interface InputStateStorage : InputStateProvider {

    override val inputProvider: InputProvider

    val data: InputStateMap

    fun copy(): InputStateStorage

    fun mutableCopy(): MutableInputStateStorage

    override fun get(input: InputScalar): LogicState {
        // Check that an input with the given name exists.
        require(inputProvider.hasInput(input.name)) { "Unknown input ${input.name}" }

        // Return the value of the input.
        return data[input.name]!![0]
    }

    override fun get(inputVector: InputVector): Array<LogicState> {
        // Check that an input with the given name exists.
        require(inputProvider.hasInput(inputVector.name)) { "Unknown input vector ${inputVector.name}" }

        // Return the value of the input.
        return data[inputVector.name]!!
    }

    override fun get(inputVector: InputVector, index: Int): LogicState {
        return get(inputVector)[index]
    }
}

internal class MutableInputStateStorage(
    override val inputProvider: InputProvider,
    override val data: MutableInputStateMap,
) : InputStateStorage, MutableInputStateProvider {

    override fun copy(): MutableInputStateStorage = mutableCopy()

    override fun mutableCopy(): MutableInputStateStorage {
        return MutableInputStateStorage(inputProvider, data.toMutableMap())
    }

    override fun set(input: InputScalar, value: LogicState) {
        // Check that an input with the given name exists.
        require(inputProvider.hasInput(input.name)) { "Unknown input ${input.name}" }

        // Update the value of the input.
        data[input.name]!![0] = value
    }

    override fun set(inputVector: InputVector, value: Array<LogicState>) {
        // Check that an input with the given name exists.
        require(inputProvider.hasInput(inputVector.name)) { "Unknown input vector ${inputVector.name}" }

        // Update the value of the input.
        data[inputVector.name] = value
    }

    override fun set(inputVector: InputVector, index: Int, value: LogicState) {
        // Check that an input with the given name exists.
        require(inputProvider.hasInput(inputVector.name)) { "Unknown input vector ${inputVector.name}" }

        // Return the value of the input.
        data[inputVector.name]!![index] = value
    }
}

internal fun inputStateStorage(variableProvider: InputProvider, data: InputStateMap): InputStateStorage {
    return mutableInputStateStorage(variableProvider, data)
}

internal fun inputStateStorage(variableProvider: InputProvider, dataProvider: InputStateProvider): InputStateStorage {
    return mutableInputStateStorage(variableProvider, dataProvider)
}

internal fun mutableInputStateStorage(variableProvider: InputProvider, data: InputStateMap): MutableInputStateStorage {
    val processedData: MutableInputStateMap = mutableMapOf()
    for (input in variableProvider.inputs()) {
        // Check that the input has an assigned value.
        require(input.name in data) { "No value provided for the input ${input.name}" }

        // Get the value from the map.
        val value = data[input.name]!!

        // Put value into map.
        processedData[input.name] = value
    }
    return MutableInputStateStorage(variableProvider, processedData)
}

internal fun mutableInputStateStorage(variableProvider: InputProvider, dataProvider: InputStateProvider): MutableInputStateStorage {
    val processedData: MutableInputStateMap = mutableMapOf()
    for (input in variableProvider.inputs()) {
        // Check that the input has an assigned value.
        require(dataProvider.inputProvider.hasVariable(input)) { "No value provided for the input ${input.name}" }

        // Get the value from the provider.
        val value = when (input) {
            is InputScalar -> arrayOf(dataProvider[input])
            is InputVector -> dataProvider[input]
            else -> throw IllegalStateException()
        }

        // Put value into map.
        processedData[input.name] = value
    }
    return MutableInputStateStorage(variableProvider, processedData)
}

internal interface OutputStateStorage : OutputStateProvider {

    override val outputProvider: OutputProvider

    val data: OutputStateMap

    fun copy(): OutputStateStorage

    fun mutableCopy(): MutableOutputStateStorage

    override fun get(output: OutputScalar): LogicState {
        // Check that an output with the given name exists.
        require(outputProvider.hasOutput(output.name)) { "Unknown output ${output.name}" }

        // Return the value of the output.
        return data[output.name]!![0]
    }

    override fun get(outputVector: OutputVector): Array<LogicState> {
        // Check that an output with the given name exists.
        require(outputProvider.hasOutput(outputVector.name)) { "Unknown output vector ${outputVector.name}" }

        // Return the value of the output.
        return data[outputVector.name]!!
    }

    override fun get(outputVector: OutputVector, index: Int): LogicState {
        return get(outputVector)[index]
    }
}

internal class MutableOutputStateStorage(
    override val outputProvider: OutputProvider,
    override val data: MutableOutputStateMap,
) : OutputStateStorage, MutableOutputStateProvider {

    override fun copy(): OutputStateStorage = mutableCopy()

    override fun mutableCopy(): MutableOutputStateStorage {
        return MutableOutputStateStorage(outputProvider, data.toMutableMap())
    }

    override fun set(output: OutputScalar, value: LogicState) {
        // Check that an output with the given name exists.
        require(outputProvider.hasOutput(output.name)) { "Unknown output ${output.name}" }

        // Update the value of the output.
        data[output.name]!![0] = value
    }

    override fun set(outputVector: OutputVector, value: Array<LogicState>) {
        // Check that an output with the given name exists.
        require(outputProvider.hasOutput(outputVector.name)) { "Unknown output vector ${outputVector.name}" }

        // Update the value of the output.
        data[outputVector.name] = value
    }

    override fun set(outputVector: OutputVector, index: Int, value: LogicState) {
        // Check that an output with the given name exists.
        require(outputProvider.hasOutput(outputVector.name)) { "Unknown output vector ${outputVector.name}" }

        // Return the value of the output.
        data[outputVector.name]!![index] = value
    }
}

internal fun outputStateStorage(variableProvider: OutputProvider, data: OutputStateMap): OutputStateStorage {
    return mutableOutputStateStorage(variableProvider, data)
}

internal fun outputStateStorage(variableProvider: OutputProvider, dataProvider: OutputStateProvider): OutputStateStorage {
    return mutableOutputStateStorage(variableProvider, dataProvider)
}

internal fun mutableOutputStateStorage(variableProvider: OutputProvider, data: OutputStateMap): MutableOutputStateStorage {
    val processedData: MutableOutputStateMap = mutableMapOf()
    for (output in variableProvider.outputs()) {
        // Check that the output has an assigned value.
        require(output.name in data) { "No value provided for the output ${output.name}" }

        // Get the value from the map.
        val value = data[output.name]!!

        // Put value into map.
        processedData[output.name] = value
    }
    return MutableOutputStateStorage(variableProvider, processedData)
}

internal fun mutableOutputStateStorage(variableProvider: OutputProvider, dataProvider: OutputStateProvider): MutableOutputStateStorage {
    val processedData: MutableOutputStateMap = mutableMapOf()
    for (output in variableProvider.outputs()) {
        // Check that the output has an assigned value.
        require(dataProvider.outputProvider.hasVariable(output)) { "No value provided for the output ${output.name}" }

        // Get the value from the provider.
        val value = when (output) {
            is OutputScalar -> arrayOf(dataProvider[output])
            is OutputVector -> dataProvider[output]
            else -> throw IllegalStateException()
        }

        // Put value into map.
        processedData[output.name] = value
    }
    return MutableOutputStateStorage(variableProvider, processedData)
}
