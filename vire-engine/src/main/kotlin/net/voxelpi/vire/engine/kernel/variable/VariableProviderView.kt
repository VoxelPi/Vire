package net.voxelpi.vire.engine.kernel.variable

public class VariableProviderView(
    public val variableProvider: VariableProvider,
    public val filter: (variable: Variable<*>) -> Boolean,
) : VariableProvider {

    override fun variables(): Collection<Variable<*>> {
        return variableProvider.variables().filter(filter)
    }

    override fun variable(name: String): Variable<*>? {
        val variable = variableProvider.variable(name) ?: return null
        if (!filter(variable)) {
            return null
        }
        return variable
    }
}
