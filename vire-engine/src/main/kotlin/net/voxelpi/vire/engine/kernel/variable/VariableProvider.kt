package net.voxelpi.vire.engine.kernel.variable

/**
 * A type that provides kernel variables of any type.
 */
public interface VariableProvider {

    /**
     * Returns all registered variables.
     */
    public fun variables(): Collection<Variable<*>>

    /**
     * Returns the variable with the given [name].
     */
    public fun variable(name: String): Variable<*>?

    /**
     * Checks if there is a registered variable with the given [name].
     */
    public fun hasVariable(name: String): Boolean = variable(name) != null

    /**
     * Returns all registered vector variables
     */
    public fun vectorVariables(): Collection<VectorVariable<*>> = variablesOfKind<VectorVariable<*>>()

    /**
     * Checks if there is a registered variable with the given [name].
     */
    public fun hasScalarVariable(name: String): Boolean = hasVariableOfKind<ScalarVariable<*>>(name)

    /**
     * Checks if there is a registered variable with the given [name].
     */
    public fun hasVectorVariable(name: String): Boolean = hasVariableOfKind<VectorVariable<*>>(name)
}

/**
 * Returns all registered variables of kind [K].
 */
public inline fun <reified K : Variable<*>> VariableProvider.variablesOfKind(): Collection<K> {
    return variables().filterIsInstance<K>()
}

/**
 * Returns the variable of kind [K] with the given [name].
 * If no variable with that name exists or the variable is not of kind [K], null is returned.
 */
public inline fun <reified K : Variable<*>> VariableProvider.variableOfKind(name: String): K? {
    val variable = variable(name) ?: return null
    if (variable !is K) {
        return null
    }
    return variable
}

/**
 * Checks if there is a registered variable with the given [name] and kind [K].
 */
public inline fun <reified K : Variable<*>> VariableProvider.hasVariableOfKind(name: String): Boolean {
    return variableOfKind<K>(name) != null
}

/**
 * A type that provides kernel parameters.
 */
public interface ParameterProvider : VariableProvider {

    /**
     * Returns all registered parameters.
     */
    public fun parameters(): Collection<Parameter<*>> = variablesOfKind()

    /**
     * Returns the parameter with the given [name].
     */
    public fun parameter(name: String): Parameter<*>? = variableOfKind(name)

    /**
     * Checks if there is a registered parameter with the given [name].
     */
    public fun hasParameter(name: String): Boolean = hasVariableOfKind<Parameter<*>>(name)
}

/**
 * A type that provides kernel settings.
 */
public interface SettingProvider : VariableProvider {

    /**
     * Returns all registered settings.
     */
    public fun settings(): Collection<Setting<*>> = variablesOfKind()

    /**
     * Returns the setting with the given [name].
     */
    public fun setting(name: String): Setting<*>? = variableOfKind(name)

    /**
     * Checks if there is a registered setting with the given [name].
     */
    public fun hasSetting(name: String): Boolean = hasVariableOfKind<Setting<*>>(name)
}

/**
 * A type that provides kernel fields.
 */
public interface FieldProvider : VariableProvider {

    /**
     * Returns all registered fields.
     */
    public fun fields(): Collection<Field<*>> = variablesOfKind()

    /**
     * Returns the field with the given [name].
     */
    public fun field(name: String): Field<*>? = variableOfKind(name)

    /**
     * Checks if there is a registered field with the given [name].
     */
    public fun hasField(name: String): Boolean = hasVariableOfKind<Field<*>>(name)
}

/**
 * A type that provides kernel inputs.
 */
public interface InputProvider : VariableProvider {

    /**
     * Returns all inputs that are registered on the kernel.
     */
    public fun inputs(): Collection<Input> = variablesOfKind()

    /**
     * Returns all input vectors that are registered on the kernel.
     */
    public fun inputVectors(): Collection<InputVector> = variablesOfKind()

    /**
     * Returns the input with the given [name].
     */
    public fun input(name: String): Input? = variableOfKind(name)

    /**
     * Checks if the kernel has an input with the given [name].
     */
    public fun hasInput(name: String): Boolean = hasVariableOfKind<Input>(name)
}

/**
 * A type that provides kernel outputs.
 */
public interface OutputProvider : VariableProvider {

    /**
     * Returns all outputs that are registered on the kernel.
     */
    public fun outputs(): Collection<Output> = variablesOfKind()

    /**
     * Returns all output vectors that are registered on the kernel.
     */
    public fun outputVectors(): Collection<OutputVector> = variablesOfKind()

    /**
     * Returns the output with the given [name].
     */
    public fun output(name: String): Output? = variableOfKind(name)

    /**
     * Checks if the kernel has an output with the given [name].
     */
    public fun hasOutput(name: String): Boolean = hasVariableOfKind<Output>(name)
}