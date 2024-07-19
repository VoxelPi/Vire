package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.MutableParameterStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateStorageWrapper

internal interface ParameterStatePatch : PartialParameterStateProvider {

    override val variableProvider: VariableProvider

    val data: ParameterStateMap

    fun copy(): ParameterStatePatch

    fun mutableCopy(): MutableParameterStatePatch

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameter: Parameter<T>): T {
        // Check that a parameter with the given name exists.
        require(variableProvider.hasParameter(parameter)) { "Unknown parameter ${parameter.name}" }

        // Check that the parameter has been initialized.
        require(parameter.name in data) { "Usage of uninitialized parameter ${parameter.name}" }

        // Return the value of the parameter.
        return data[parameter.name] as T
    }

    override fun hasValue(parameter: Parameter<*>): Boolean {
        return parameter.name in data
    }

    fun isComplete(): Boolean {
        return variableProvider.parameters().all { hasValue(it) }
    }
}

internal class MutableParameterStatePatch(
    override val variableProvider: VariableProvider,
    override val data: MutableParameterStateMap,
) : ParameterStatePatch, MutablePartialParameterStateProvider {

    override fun copy(): ParameterStatePatch = mutableCopy()

    override fun mutableCopy(): MutableParameterStatePatch {
        return MutableParameterStatePatch(variableProvider, data.toMutableMap())
    }

    override fun <T> set(parameter: Parameter<T>, value: T) {
        // Check that a parameter with the given name exists.
        require(variableProvider.hasParameter(parameter)) { "Unknown parameter ${parameter.name}" }

        // Check that the value is valid for the specified parameter.
        require(parameter.isValidValue(value)) { "Value $parameter does not meet the requirements for the parameter ${parameter.name}" }

        // Update the value of the parameter.
        data[parameter.name] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun update(data: ParameterStateMap) {
        for ((parameterName, value) in data) {
            // Check that only existing parameters are specified.
            val parameter = variableProvider.parameter(parameterName) as Parameter<Any?>?
                ?: throw IllegalArgumentException("Unknown parameter '$parameterName'")

            // Update the value of the parameter.
            this[parameter] = value
        }
    }
}

internal fun parameterStatePatch(variableProvider: VariableProvider, data: ParameterStateMap): ParameterStatePatch {
    return mutableParameterStatePatch(variableProvider, data)
}

internal fun parameterStatePatch(variableProvider: VariableProvider, dataProvider: ParameterStateProvider): ParameterStatePatch {
    return mutableParameterStatePatch(variableProvider, dataProvider)
}

internal fun mutableParameterStatePatch(variableProvider: VariableProvider, data: ParameterStateMap): MutableParameterStatePatch {
    val processedData: MutableParameterStateMap = mutableMapOf()
    for (parameter in variableProvider.parameters()) {
        // Check if the parameter has an assigned value.
        if (parameter.name !in data) {
            continue
        }

        // Get the value from the map.
        val value = data[parameter.name]

        // Check that the assigned value is valid for the given parameter.
        require(parameter.isValidTypeAndValue(value)) { "Invalid value for the parameter ${parameter.name}" }

        // Put value into map.
        processedData[parameter.name] = value
    }
    return MutableParameterStatePatch(variableProvider, processedData)
}

internal fun mutableParameterStatePatch(
    variableProvider: VariableProvider,
    dataProvider: ParameterStateProvider,
): MutableParameterStatePatch {
    val processedData: MutableParameterStateMap = mutableMapOf()
    for (parameter in variableProvider.parameters()) {
        // Check if the parameter has an assigned value.
        if (!dataProvider.variableProvider.hasVariable(parameter)) {
            continue
        }
        if (dataProvider is ParameterStatePatch && !dataProvider.hasValue(parameter)) {
            continue
        }
        if (dataProvider is ParameterStateStorageWrapper && !dataProvider.hasValue(parameter)) {
            continue
        }

        // Get the value from the provider.
        val value = dataProvider[parameter]

        // Check that the assigned value is valid for the given parameter.
        require(parameter.isValidTypeAndValue(value)) { "Invalid value for the parameter ${parameter.name}" }

        // Put value into map.
        processedData[parameter.name] = value
    }
    return MutableParameterStatePatch(variableProvider, processedData)
}
