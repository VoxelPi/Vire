package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.UninitializedVariableException
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.MutableParameterStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateStorage

/**
 * A collection that stores the state of some parameters of the given [variableProvider].
 */
public open class ParameterStatePatch(
    final override val variableProvider: VariableProvider,
    initialData: ParameterStateMap = emptyMap(),
) : PartialParameterStateProvider {

    init {
        for ((parameterName, parameterState) in initialData) {
            val parameter = variableProvider.parameter(parameterName)
                ?: throw IllegalStateException("Data specified for unknown parameter \"$parameterName\".")

            require(parameter.isValidTypeAndValue(parameterState)) { "Invalid value specified for parameter \"$parameterName\"." }
        }
    }

    protected open val data: ParameterStateMap = initialData.toMap()

    public constructor(variableProvider: VariableProvider, initialData: PartialParameterStateProvider) : this(
        variableProvider,
        variableProvider.parameters().filter { initialData.hasValue(it) }.associate { it.name to initialData[it] }
    )

    /**
     * Creates a copy of this patch.
     */
    public fun copy(): ParameterStatePatch {
        return ParameterStatePatch(variableProvider, data)
    }

    /**
     * Creates a mutable copy of this patch.
     */
    public fun mutableCopy(): MutableParameterStatePatch {
        return MutableParameterStatePatch(variableProvider, data)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameter: Parameter<T>): T {
        // Check that a parameter with the given name exists.
        require(variableProvider.hasParameter(parameter)) { "Unknown parameter ${parameter.name}" }

        // Check that the parameter has been initialized.
        if (parameter.name !in data) {
            throw UninitializedVariableException(parameter)
        }

        // Return the value of the parameter.
        return data[parameter.name] as T
    }

    override fun hasValue(parameter: Parameter<*>): Boolean {
        return parameter.name in data
    }

    override fun allParametersSet(): Boolean {
        return variableProvider.parameters().all { hasValue(it) }
    }

    /**
     * Creates a parameter state storage using the set data.
     * All parameters must have a set value otherwise this operation fails.
     */
    public fun createStorage(): ParameterStateStorage {
        return ParameterStateStorage(variableProvider, data)
    }
}

/**
 * A mutable collection that stores the state of some parameters of the given [variableProvider].
 */
public class MutableParameterStatePatch(
    variableProvider: VariableProvider,
    initialData: ParameterStateMap = emptyMap(),
) : ParameterStatePatch(variableProvider, initialData), MutablePartialParameterStateProvider {

    override val data: MutableParameterStateMap = initialData.toMutableMap()

    public constructor(variableProvider: VariableProvider, initialData: PartialParameterStateProvider) : this(
        variableProvider,
        variableProvider.parameters().filter { initialData.hasValue(it) }.associate { it.name to initialData[it] }
    )

    override fun <T> set(parameter: Parameter<T>, value: T) {
        // Check that a parameter with the given name exists.
        require(variableProvider.hasParameter(parameter)) { "Unknown parameter ${parameter.name}" }

        // Check that the value is valid for the specified parameter.
        require(parameter.isValidTypeAndValue(value)) { "Value $value does not meet the requirements for the parameter ${parameter.name}" }

        // Update the value of the parameter.
        data[parameter.name] = value
    }
}
