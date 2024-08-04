package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.patch.MutableFieldStatePatch
import net.voxelpi.vire.engine.kernel.variable.patch.MutableOutputStatePatch
import net.voxelpi.vire.engine.kernel.variable.patch.MutableParameterStatePatch
import net.voxelpi.vire.engine.kernel.variable.patch.MutableSettingStatePatch
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.storage.InputStateStorage

/**
 * A type that provides kernel variables of any type.
 */
public interface VariableProvider : Iterable<Variable<*>> {

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
     * Checks if the given [variable] is registered.
     */
    public fun hasVariable(variable: Variable<*>): Boolean = variable(variable.name) == variable

    /**
     * Checks if there is a registered variable with the given [name].
     */
    public operator fun contains(name: String): Boolean = variable(name) != null

    /**
     * Checks if the given [variable] is registered.
     */
    public operator fun contains(variable: Variable<*>): Boolean = variable(variable.name) == variable

    /**
     * Get the iterator over all registered variables.
     */
    override fun iterator(): Iterator<Variable<*>> = variables().iterator()

    /**
     * Returns all registered vector variables
     */
    public fun vectorVariables(): Collection<VectorVariable<*>> = variablesOfKind<VectorVariable<*>>()

    /**
     * Returns the vector variable with the given [name].
     */
    public fun vectorVariable(name: String): VectorVariable<*>? = variableOfKind(name)

    /**
     * Checks if there is a registered variable with the given [name].
     */
    public fun hasScalarVariable(name: String): Boolean = hasVariableOfKind<ScalarVariable<*>>(name)

    /**
     * Checks if the given [parameter] is registered.
     */
    public fun hasVectorVariable(vector: VectorVariable<*>): Boolean = vectorVariable(vector.name) == vector

    /**
     * Checks if there is a registered variable with the given [name].
     */
    public fun hasVectorVariable(name: String): Boolean = hasVariableOfKind<VectorVariable<*>>(name)

    /**
     * Returns all registered parameters.
     */
    public fun parameters(): Collection<Parameter<*>> = variablesOfKind()

    /**
     * Returns the parameter with the given [name].
     */
    public fun parameter(name: String): Parameter<*>? = variableOfKind(name)

    /**
     * Checks if the given [parameter] is registered.
     */
    public fun hasParameter(parameter: Parameter<*>): Boolean = parameter(parameter.name) == parameter

    /**
     * Checks if there is a registered parameter with the given [name].
     */
    public fun hasParameter(name: String): Boolean = hasVariableOfKind<Parameter<*>>(name)

    /**
     * Returns all registered settings.
     */
    public fun settings(): Collection<Setting<*>> = variablesOfKind()

    /**
     * Returns the setting with the given [name].
     */
    public fun setting(name: String): Setting<*>? = variableOfKind(name)

    /**
     * Checks if the given [setting] is registered.
     */
    public fun hasSetting(setting: Setting<*>): Boolean = setting(setting.name) == setting

    /**
     * Checks if there is a registered setting with the given [name].
     */
    public fun hasSetting(name: String): Boolean = hasVariableOfKind<Setting<*>>(name)

    /**
     * Returns all registered fields.
     */
    public fun fields(): Collection<Field<*>> = variablesOfKind()

    /**
     * Returns the field with the given [name].
     */
    public fun field(name: String): Field<*>? = variableOfKind(name)

    /**
     * Checks if the given [field] is registered.
     */
    public fun hasField(field: Field<*>): Boolean = field(field.name) == field

    /**
     * Checks if there is a registered field with the given [name].
     */
    public fun hasField(name: String): Boolean = hasVariableOfKind<Field<*>>(name)

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
     * Checks if the given [input] is registered.
     */
    public fun hasInput(input: Input): Boolean = input(input.name) == input

    /**
     * Checks if the kernel has an input with the given [name].
     */
    public fun hasInput(name: String): Boolean = hasVariableOfKind<Input>(name)

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
     * Checks if the given [output] is registered.
     */
    public fun hasOutput(output: Output): Boolean = output(output.name) == output

    /**
     * Checks if the kernel has an output with the given [name].
     */
    public fun hasOutput(name: String): Boolean = hasVariableOfKind<Output>(name)

    /**
     * Check whether this variable provider is a subset of the given [variableProvider].
     */
    public fun isSubsetOf(variableProvider: VariableProvider): Boolean {
        return this.variables().all(variableProvider::contains)
    }

    /**
     * Check whether this variable provider is a superset of the given [variableProvider].
     */
    public fun isSupersetOf(variableProvider: VariableProvider): Boolean {
        return variableProvider.all(this::contains)
    }

    /**
     * Generates a new [PartialParameterStateProvider] with the default value of each parameter.
     */
    @Suppress("UNCHECKED_CAST")
    public fun defaultParameterStates(): PartialParameterStateProvider {
        val parameterStates = MutableParameterStatePatch(this)
        for (parameter in parameters()) {
            val initialization = parameter.initialization ?: continue
            parameterStates[parameter as Parameter<Any?>] = initialization.invoke()
        }
        return parameterStates
    }

    /**
     * Generates a new [PartialParameterStateProvider] with the default value of each parameter.
     */
    @Suppress("UNCHECKED_CAST")
    public fun defaultSettingStates(
        vectorSizes: VectorSizeProvider,
        parameterStates: ParameterStateProvider,
    ): PartialSettingStateProvider {
        val settingInitializationContext = SettingInitializationContext(this, vectorSizes, parameterStates)

        val settingStates = MutableSettingStatePatch(this)
        for (setting in settings()) {
            val initialization = setting.initialization ?: continue
            settingStates[setting as Setting<Any?>] = initialization.invoke(settingInitializationContext)
        }
        return settingStates
    }

    @Suppress("UNCHECKED_CAST")
    public fun defaultFieldStates(
        vectorSizes: VectorSizeProvider,
        parameterStates: ParameterStateProvider,
        settingStates: SettingStateProvider,
    ): PartialFieldStateProvider {
        val fieldInitializationContext = FieldInitializationContext(this, vectorSizes, parameterStates, settingStates)

        val fieldStates = MutableFieldStatePatch(this)
        for (field in fields()) {
            val initialization = field.initialization ?: continue
            fieldStates[field as Field<Any?>] = initialization.invoke(fieldInitializationContext)
        }
        return fieldStates
    }

    public fun defaultOutputStates(
        vectorSizes: VectorSizeProvider,
        parameterStates: ParameterStateProvider,
        settingStates: SettingStateProvider,
    ): PartialOutputStateProvider {
        val scalarInitializationContext = OutputScalarInitializationContext(this, vectorSizes, parameterStates, settingStates)
        val vectorInitializationContext = OutputVectorInitializationContext(this, vectorSizes, parameterStates, settingStates)

        val outputStates = MutableOutputStatePatch(this)
        for (output in outputs()) {
            when (output) {
                is OutputScalar -> {
                    outputStates[output] = output.initialization(scalarInitializationContext)
                }
                is OutputVector -> {
                    outputStates[output] = Array(vectorSizes.size(output)) { index ->
                        output.initialization(vectorInitializationContext, index)
                    }
                }
                is OutputVectorElement -> {
                    throw UnsupportedOperationException("Vector elements cannot be initialized directly")
                }
            }
        }
        return outputStates
    }

    public fun defaultInputStates(
        vectorSizes: VectorSizeProvider,
    ): InputStateProvider {
        return InputStateStorage(
            this,
            this.inputs().associate { input ->
                val size = when (input) {
                    is InputScalar -> 1
                    is InputVector -> vectorSizes.size(input)
                    is InputVectorElement -> throw IllegalStateException("Vector elements are not allowed")
                }
                input.name to Array(size) { LogicState.EMPTY }
            },
        )
    }
}

public interface MutableVariableProvider : VariableProvider {

    /**
     * Declares the given [variable].
     */
    public fun <V : Variable<*>> declare(variable: V): V
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
