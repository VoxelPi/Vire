package net.voxelpi.vire.engine.kernel.variable

public interface VariableRegistry : VariableProvider

public interface MutableVariableRegistry : VariableRegistry {

    public fun registerVariable(variable: Variable<*>)

    public fun unregisterVariable(variable: Variable<*>)
}

internal class MutableVariableRegistryImpl(
    variables: Collection<Variable<*>> = emptyList(),
) : MutableVariableRegistry {

    val variables: MutableMap<String, Variable<*>> = variables.associateBy { it.name }.toMutableMap()

    override fun variables(): Collection<Variable<*>> {
        return variables.values
    }

    override fun variable(name: String): Variable<*>? {
        return variables[name]
    }

    override fun registerVariable(variable: Variable<*>) {
        require(variable.name !in variables) { "A variable with the name ${variable.name} already exists." }

        variables[variable.name] = variable
    }

    override fun unregisterVariable(variable: Variable<*>) {
        val registeredVariable = variables[variable.name] ?: return

        require(registeredVariable == variable) { "A different variable is registered under the name ${variable.name}." }

        variables.remove(variable.name)
    }
}
