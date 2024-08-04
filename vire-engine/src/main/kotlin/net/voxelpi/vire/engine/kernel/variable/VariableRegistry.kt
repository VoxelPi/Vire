package net.voxelpi.vire.engine.kernel.variable

public open class VariableRegistry(
    variables: Collection<Variable<*>> = emptyList(),
) : VariableProvider {

    protected open val variables: Map<String, Variable<*>> = variables.associateBy { it.name }

    override fun variables(): Collection<Variable<*>> {
        return variables.values
    }

    override fun variable(name: String): Variable<*>? {
        return variables[name]
    }
}

public class MutableVariableRegistry(
    variables: Collection<Variable<*>> = emptyList(),
) : VariableRegistry(variables), MutableVariableProvider {

    override val variables: MutableMap<String, Variable<*>> = variables.associateBy { it.name }.toMutableMap()

    override fun <V : Variable<*>> declare(variable: V): V {
        return registerVariable(variable)
    }

    public fun <V : Variable<*>> registerVariable(variable: V): V {
        require(variable.name !in variables) { "A variable with the name \"${variable.name}\" already exists" }
        variables[variable.name] = variable
        return variable
    }

    public fun <V : Variable<*>> unregisterVariable(variable: V): V? {
        val registeredVariable = variables[variable.name] ?: return null
        require(registeredVariable == variable) { "A different variable is registered under the name ${variable.name}." }
        variables.remove(variable.name)
        return variable
    }
}
