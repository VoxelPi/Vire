package net.voxelpi.vire.api.simulation.component

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.library.Library
import net.voxelpi.vire.api.simulation.network.NetworkState

/**
 * The state machine of the component.
 * It is responsible for reading the components inputs and updating its outputs.
 */
abstract class StateMachine(
    val identifier: Identifier,
) {

    constructor(library: Library, id: String) : this(Identifier(library.id, id))

    /**
     * All registered parameters of the state machine.
     */
    protected val parameters: MutableMap<String, StateMachineParameter<*>> = mutableMapOf()

    /**
     * All registered variables of the state machine.
     */
    protected val variables: MutableMap<String, StateMachineVariable<*>> = mutableMapOf()

    /**
     * All registered inputs of the state machine.
     */
    protected val inputs: MutableMap<String, StateMachineInput> = mutableMapOf()

    /**
     * All registered outputs of the state machine.
     */
    protected val outputs: MutableMap<String, StateMachineOutput> = mutableMapOf()

    /**
     * Initializes the state machine.
     */
    open fun configure(context: StateMachineContext) {}

    /**
     * Updates the state machine.
     */
    open fun tick(context: StateMachineContext) {}

    /**
     * Returns all registered parameters of the state machine.
     */
    fun parameters(): List<StateMachineParameter<*>> {
        return parameters.values.toList()
    }

    /**
     * Returns all registered variables of the state machine.
     */
    fun variables(): List<StateMachineVariable<*>> {
        return variables.values.toList()
    }

    /**
     * Returns all registered inputs of the state machine.
     */
    fun inputs(): List<StateMachineInput> {
        return inputs.values.toList()
    }

    /**
     * Returns all registered outputs of the state machine.
     */
    fun outputs(): List<StateMachineOutput> {
        return outputs.values.toList()
    }

    /**
     * Returns the parameter with the given [name].
     */
    fun parameter(name: String): StateMachineParameter<*>? {
        return parameters[name]
    }

    /**
     * Returns the variable with the given [name].
     */
    fun variable(name: String): StateMachineVariable<*>? {
        return variables[name]
    }

    /**
     * Returns the input with the given [name].
     */
    fun input(name: String): StateMachineInput? {
        return inputs[name]
    }

    /**
     * Returns the output with the given [name].
     */
    fun output(name: String): StateMachineOutput? {
        return outputs[name]
    }

    /**
     * Declares a new parameter for the state machine.
     */
    protected inline fun <reified T> declare(parameter: StateMachineParameter<T>): StateMachineParameter<T> {
        require(!parameters.containsKey(parameter.name)) { "The state machine already has a parameter with the name ${parameter.name}." }
        parameters[parameter.name] = parameter
        return parameter
    }

    /**
     * Declares a new variable for the state machine.
     */
    protected inline fun <reified T> declare(variable: StateMachineVariable<T>): StateMachineVariable<T> {
        require(!variables.containsKey(variable.name)) { "The state machine already has a variable with the name ${variable.name}." }
        variables[variable.name] = variable
        return variable
    }

    /**
     * Declares a new input for the state machine.
     */
    protected fun declare(input: StateMachineInput): StateMachineInput {
        require(!variables.containsKey(input.name)) { "The state machine already has an input with the name ${input.name}." }
        inputs[input.name] = input
        return input
    }

    /**
     * Declares a new output for the state machine.
     */
    protected fun declare(output: StateMachineOutput): StateMachineOutput {
        require(!variables.containsKey(output.name)) { "The state machine already has an output with the name ${output.name}." }
        outputs[output.name] = output
        return output
    }

    /**
     * Declares a new parameter for the state machine.
     */
    protected inline fun <reified T> declareParameter(
        name: String,
        initialValue: T,
        noinline predicate: (value: T, context: StateMachineParameterContext) -> Boolean = { _, _ -> true },
    ): StateMachineParameter<T> {
        return declare<T>(StateMachineParameter(name, initialValue, predicate))
    }

    /**
     * Declares a new variable for the state machine.
     */
    protected inline fun <reified T> declareVariable(name: String, initialValue: T): StateMachineVariable<T> {
        return declare<T>(StateMachineVariable(name, initialValue))
    }

    /**
     * Declares a new input for the state machine.
     */
    protected fun declareInput(name: String, initialSize: Int = 1): StateMachineInput {
        return declare(StateMachineInput(name, initialSize))
    }

    /**
     * Declares a new output for the state machine.
     */
    protected fun declareOutput(name: String, initialSize: Int = 1, initialValue: NetworkState = NetworkState.None): StateMachineOutput {
        return declare(StateMachineOutput(name, initialSize, initialValue))
    }
}
