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
     * Returns all registered vector variables
     */
    public fun vectorVariables(): Collection<VectorVariable<*>> = variables().filterIsInstance<VectorVariable<*>>()

    /**
     * Returns the variable with the given [name].
     */
    public fun variable(name: String): Variable<*>?

    /**
     * Checks if there is a registered variable with the given [name].
     */
    public fun hasVariable(name: String): Boolean = variable(name) != null

    /**
     * Checks if there is a registered variable with the given [name].
     */
    public fun hasScalarVariable(name: String): Boolean = (variable(name) as? ScalarVariable<*>) != null

    /**
     * Checks if there is a registered variable with the given [name].
     */
    public fun hasVectorVariable(name: String): Boolean = (variable(name) as? VectorVariable<*>) != null
}

/**
 * A type that provides kernel parameters.
 */
public interface ParameterProvider : VariableProvider {

    /**
     * Returns all registered parameters.
     */
    public fun parameters(): Collection<Parameter<*>>

    /**
     * Returns the parameter with the given [name].
     */
    public fun parameter(name: String): Parameter<*>?

    /**
     * Checks if there is a registered parameter with the given [name].
     */
    public fun hasParameter(name: String): Boolean = parameter(name) != null
}

/**
 * A type that provides kernel settings.
 */
public interface SettingProvider : VariableProvider {

    /**
     * Returns all registered settings.
     */
    public fun settings(): Collection<Setting<*>>

    /**
     * Returns the setting with the given [name].
     */
    public fun setting(name: String): Setting<*>?

    /**
     * Checks if there is a registered setting with the given [name].
     */
    public fun hasSetting(name: String): Boolean = setting(name) != null
}

/**
 * A type that provides kernel fields.
 */
public interface FieldProvider : VariableProvider {

    /**
     * Returns all registered fields.
     */
    public fun fields(): Collection<Field<*>>

    /**
     * Returns the field with the given [name].
     */
    public fun field(name: String): Field<*>?

    /**
     * Checks if there is a registered field with the given [name].
     */
    public fun hasField(name: String): Boolean = field(name) != null
}

/**
 * A type that provides kernel inputs.
 */
public interface InputProvider : VariableProvider {

    /**
     * Returns all inputs that are registered on the kernel.
     */
    public fun inputs(): Collection<Input>

    /**
     * Returns all input vectors that are registered on the kernel.
     */
    public fun inputVectors(): Collection<InputVector> = inputs().filterIsInstance<InputVector>()

    /**
     * Returns the input with the given [name].
     */
    public fun input(name: String): Input?

    /**
     * Checks if the kernel has an input with the given [name].
     */
    public fun hasInput(name: String): Boolean = input(name) != null
}

/**
 * A type that provides kernel outputs.
 */
public interface OutputProvider : VariableProvider {

    /**
     * Returns all outputs that are registered on the kernel.
     */
    public fun outputs(): Collection<Output>

    /**
     * Returns all output vectors that are registered on the kernel.
     */
    public fun outputVectors(): Collection<OutputVector> = outputs().filterIsInstance<OutputVector>()

    /**
     * Returns the output with the given [name].
     */
    public fun output(name: String): Output?

    /**
     * Checks if the kernel has an output with the given [name].
     */
    public fun hasOutput(name: String): Boolean = output(name) != null
}
