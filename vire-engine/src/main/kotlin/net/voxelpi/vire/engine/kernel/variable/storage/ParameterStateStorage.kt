package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider

public typealias ParameterStateMap = Map<String, Any?>

public typealias MutableParameterStateMap = MutableMap<String, Any?>

/**
 * A collection that stores the state of all parameters of the given [variableProvider].
 */
public open class ParameterStateStorage(
    final override val variableProvider: VariableProvider,
    initialData: ParameterStateMap,
) : ParameterStateProvider {

    init {
        for ((parameterName, parameterState) in initialData) {
            val parameter = variableProvider.parameter(parameterName)
                ?: throw IllegalStateException("Data specified for unknown parameter \"$parameterName\".")

            require(parameter.isValidTypeAndValue(parameterState)) { "Invalid value specified for parameter \"$parameterName\"." }
        }

        val missingVariables = variableProvider.parameters().map { it.name }.filter { it !in initialData }
        require(missingVariables.isEmpty()) {
            "Missing values for the following parameters: ${missingVariables.joinToString(", ") { "\"${it}\"" } }"
        }
    }

    protected open val data: ParameterStateMap = initialData.toMap()

    public constructor(variableProvider: VariableProvider, initialData: ParameterStateProvider) : this(
        variableProvider,
        variableProvider.parameters().filter { initialData.hasValue(it) }.associate { it.name to initialData[it] }
    )

    /**
     * Creates a copy of this storage.
     */
    public fun copy(): ParameterStateStorage {
        return ParameterStateStorage(variableProvider, data)
    }

    /**
     * Creates a mutable copy of this storage.
     */
    public fun mutableCopy(): MutableParameterStateStorage {
        return MutableParameterStateStorage(variableProvider, data)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameter: Parameter<T>): T {
        // Check that a parameter with the given name exists.
        require(variableProvider.hasParameter(parameter)) { "Unknown parameter ${parameter.name}" }

        // Return the value of the parameter.
        return data[parameter.name] as T
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ParameterStateStorage) return false

        if (this.variableProvider != other.variableProvider) return false
        if (this.data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }
}

/**
 * A mutable collection that stores the state of all parameters of the given [variableProvider].
 */
public class MutableParameterStateStorage(
    variableProvider: VariableProvider,
    initialData: ParameterStateMap,
) : ParameterStateStorage(variableProvider, initialData), MutableParameterStateProvider {

    override val data: MutableParameterStateMap = initialData.toMutableMap()

    public constructor(variableProvider: VariableProvider, initialData: ParameterStateProvider) : this(
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
