package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider

internal typealias ParameterStateMap = Map<String, Any?>

internal typealias MutableParameterStateMap = MutableMap<String, Any?>

internal interface ParameterStateStorage : ParameterStateProvider {

    override val variableProvider: VariableProvider

    val data: ParameterStateMap

    fun copy(): ParameterStateStorage

    fun mutableCopy(): MutableParameterStateStorage

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameter: Parameter<T>): T {
        // Check that a parameter with the given name exists.
        require(variableProvider.hasParameter(parameter.name)) { "Unknown parameter ${parameter.name}" }

        // Return the value of the parameter.
        return data[parameter.name] as T
    }
}

internal class MutableParameterStateStorage(
    override val variableProvider: VariableProvider,
    override val data: MutableParameterStateMap,
) : ParameterStateStorage, MutableParameterStateProvider {

    override fun copy(): MutableParameterStateStorage = mutableCopy()

    override fun mutableCopy(): MutableParameterStateStorage {
        return MutableParameterStateStorage(variableProvider, data.toMutableMap())
    }

    override fun <T> set(parameter: Parameter<T>, value: T) {
        // Check that a parameter with the given name exists.
        require(variableProvider.hasParameter(parameter.name)) { "Unknown parameter ${parameter.name}" }

        // Check that the value is valid for the specified parameter.
        require(parameter.isValidValue(value)) { "Value $parameter does not meet the requirements for the parameter ${parameter.name}" }

        // Update the value of the parameter.
        data[parameter.name] = value
    }
}

internal fun parameterStateStorage(variableProvider: VariableProvider, data: ParameterStateMap): ParameterStateStorage {
    return mutableParameterStateStorage(variableProvider, data)
}

internal fun parameterStateStorage(variableProvider: VariableProvider, dataProvider: ParameterStateProvider): ParameterStateStorage {
    return mutableParameterStateStorage(variableProvider, dataProvider)
}

internal fun mutableParameterStateStorage(variableProvider: VariableProvider, data: ParameterStateMap): MutableParameterStateStorage {
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
    variableProvider: VariableProvider,
    dataProvider: ParameterStateProvider,
): MutableParameterStateStorage {
    val processedData: MutableParameterStateMap = mutableMapOf()
    for (parameter in variableProvider.parameters()) {
        // Check that the parameter has an assigned value.
        require(dataProvider.variableProvider.hasVariable(parameter)) { "No value provided for the parameter ${parameter.name}" }

        // Get the value from the provider.
        val value = dataProvider[parameter]

        // Check that the assigned value is valid for the given parameter.
        require(parameter.isValidTypeAndValue(value)) { "Invalid value for the parameter ${parameter.name}" }

        // Put value into map.
        processedData[parameter.name] = value
    }
    return MutableParameterStateStorage(variableProvider, processedData)
}
