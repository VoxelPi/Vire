package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.LogicState

internal interface VectorSizeStorageWrapper : VectorSizeProvider {

    val vectorSizeStorage: VectorSizeStorage

    override val variableProvider: VariableProvider
        get() = vectorSizeStorage.variableProvider

    override fun size(vector: VectorVariable<*>): Int {
        return vectorSizeStorage.size(vector)
    }

    override fun size(vectorName: String): Int {
        return vectorSizeStorage.size(vectorName)
    }
}

internal interface MutableVectorSizeStorageWrapper : VectorSizeStorageWrapper, MutableVectorSizeProvider {

    override val vectorSizeStorage: MutableVectorSizeStorage

    override fun resize(vector: VectorVariable<*>, size: Int) {
        vectorSizeStorage.resize(vector, size)
    }

    override fun resize(vectorName: String, size: Int) {
        vectorSizeStorage.resize(vectorName, size)
    }
}

internal interface ParameterStateStorageWrapper : ParameterStateProvider {

    val parameterStateStorage: ParameterStateStorage

    override val parameterProvider: ParameterProvider
        get() = parameterStateStorage.parameterProvider

    override fun <T> get(parameter: Parameter<T>): T {
        return parameterStateStorage[parameter]
    }
}

internal interface MutableParameterStateStorageWrapper : ParameterStateStorageWrapper, MutableParameterStateProvider {

    override val parameterStateStorage: MutableParameterStateStorage

    override fun <T> set(parameter: Parameter<T>, value: T) {
        parameterStateStorage[parameter] = value
    }
}

internal interface SettingStateStorageWrapper : SettingStateProvider {

    val settingStateStorage: SettingStateStorage

    override val settingProvider: SettingProvider
        get() = settingStateStorage.settingProvider

    override fun <T> get(setting: Setting<T>): T {
        return settingStateStorage[setting]
    }
}

internal interface MutableSettingStateStorageWrapper : SettingStateStorageWrapper, MutableSettingStateProvider {

    override val settingStateStorage: MutableSettingStateStorage

    override fun <T> set(setting: Setting<T>, value: T) {
        settingStateStorage[setting] = value
    }
}

internal interface FieldStateStorageWrapper : FieldStateProvider {

    val fieldStateStorage: FieldStateStorage

    override val fieldProvider: FieldProvider
        get() = fieldStateStorage.fieldProvider

    override fun <T> get(field: Field<T>): T {
        return fieldStateStorage[field]
    }
}

internal interface MutableFieldStateStorageWrapper : FieldStateStorageWrapper, MutableFieldStateProvider {

    override val fieldStateStorage: MutableFieldStateStorage

    override fun <T> set(field: Field<T>, value: T) {
        fieldStateStorage[field] = value
    }
}

internal interface InputStateStorageWrapper : InputStateProvider {

    val inputStateStorage: InputStateStorage

    override val inputProvider: InputProvider
        get() = inputStateStorage.inputProvider

    override fun get(input: InputScalar): LogicState {
        return inputStateStorage[input]
    }

    override fun get(inputVector: InputVector): Array<LogicState> {
        return inputStateStorage[inputVector]
    }

    override fun get(inputVector: InputVector, index: Int): LogicState {
        return inputStateStorage[inputVector, index]
    }
}

internal interface MutableInputStateStorageWrapper : InputStateStorageWrapper, MutableInputStateProvider {

    override val inputStateStorage: MutableInputStateStorage

    override fun set(input: InputScalar, value: LogicState) {
        inputStateStorage[input] = value
    }

    override fun set(inputVector: InputVector, value: Array<LogicState>) {
        inputStateStorage[inputVector] = value
    }

    override fun set(inputVector: InputVector, index: Int, value: LogicState) {
        inputStateStorage[inputVector, index] = value
    }
}

internal interface OutputStateStorageWrapper : OutputStateProvider {

    val outputStateStorage: OutputStateStorage

    override val outputProvider: OutputProvider
        get() = outputStateStorage.outputProvider

    override fun get(output: OutputScalar): LogicState {
        return outputStateStorage[output]
    }

    override fun get(outputVector: OutputVector): Array<LogicState> {
        return outputStateStorage[outputVector]
    }

    override fun get(outputVector: OutputVector, index: Int): LogicState {
        return outputStateStorage[outputVector, index]
    }
}

internal interface MutableOutputStateStorageWrapper : OutputStateStorageWrapper, MutableOutputStateProvider {

    override val outputStateStorage: MutableOutputStateStorage

    override fun set(output: OutputScalar, value: LogicState) {
        outputStateStorage[output] = value
    }

    override fun set(outputVector: OutputVector, value: Array<LogicState>) {
        outputStateStorage[outputVector] = value
    }

    override fun set(outputVector: OutputVector, index: Int, value: LogicState) {
        outputStateStorage[outputVector, index] = value
    }
}
