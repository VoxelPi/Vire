package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialVariableStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialVariableStateProvider

public interface VariableStatePatch : PartialVariableStateProvider

@Suppress("UNCHECKED_CAST")
public class MutableVariableStatePatch internal constructor(
    public val variables: Collection<Variable<*>>,
    initialData: Map<String, Any?> = emptyMap(),
) : VariableStatePatch, MutablePartialVariableStateProvider {

    init {
        for ((variableName, variableState) in initialData) {
            val variable = variableProvider.variable(variableName)
                ?: throw IllegalStateException("Data specified for unknown variable \"$variableName\".")

            require(variable.isValidTypeAndValue(variableState)) { "Invalid value specified for variable \"$variableName\"." }
        }
    }

    private val data: MutableMap<String, Any?> = initialData.toMutableMap()

    override fun contains(variable: Variable<*>): Boolean {
        if (variable !in variables) {
            return false
        }

        return variable.name in data
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T, V : Variable<T>> get(variable: V): T {
        // Check that the given variable exists.
        require(variable in variables) { "Unknown variable \"${variable.name}\"" }

        // Check that the variable has been initialized.
        require(variable.name in data) { "Cannot access state of unset variable \"${variable.name}\"" }

        // Return the value of the variable.
        return data[variable.name] as T
    }

    override fun <T, V : Variable<T>> set(variable: V, value: T) {
        // Check that the given variable exists.
        require(variableProvider.hasVariable(variable)) { "Unknown variable \"${variable.name}\"." }

        // Check that the value is valid for the specified variable.
        require(variable.isValidValue(value)) { "Value $variable does not meet the requirements for the variable \"${variable.name}\"" }

        // Update the value of the variable.
        data[variable.name] = value
    }

    public fun <T, V : Variable<T>> reset(variable: V): T? {
        // Check that the given variable exists.
        require(variableProvider.hasVariable(variable)) { "Unknown variable \"${variable.name}\"." }

        // Remove the value of the variable.
        return data.remove(variable.name) as T?
    }
}
