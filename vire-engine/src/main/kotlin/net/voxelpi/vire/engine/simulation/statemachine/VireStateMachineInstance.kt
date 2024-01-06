package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.event.simulation.statemachine.StateMachineConfigureEvent
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineIOState
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInstance
import net.voxelpi.vire.api.simulation.statemachine.StateMachineOutput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineParameter
import net.voxelpi.vire.api.simulation.statemachine.StateMachineVariable
import net.voxelpi.vire.engine.simulation.VireSimulation
import java.util.Arrays
import kotlin.reflect.KType

class VireStateMachineInstance(
    val simulation: VireSimulation,
    override val stateMachine: StateMachine,
    configuration: StateMachineInstance.ConfigurationContext.() -> Unit,
) : StateMachineInstance {

    private val initialParameterStates: Map<String, Any?>

    private val parameterStates: MutableMap<String, Any?> = mutableMapOf()
    private val variableStates: MutableMap<String, Any?> = mutableMapOf()
    private val inputStates: MutableMap<String, Array<LogicState>> = mutableMapOf()
    private val outputStates: MutableMap<String, Array<LogicState>> = mutableMapOf()

    init {
        val initialParameterStates = mutableMapOf<String, Any?>()
        for (parameter in stateMachine.parameters.values) {
            initialParameterStates[parameter.name] = parameter.initialValue
        }
        VireInitialConfigurationContext(stateMachine, initialParameterStates).configuration()
        check(stateMachine.parameters.values.all { it.isValidType(initialParameterStates[it.name]) })
        this.initialParameterStates = initialParameterStates

        reset(true)
    }

    fun reset(parameters: Boolean) {
        if (parameters) {
            // Initialize parameter states
            for (parameter in stateMachine.parameters.values) {
                parameterStates[parameter.name] = initialParameterStates[parameter.name]
            }
        }

        // Initialize variable states
        for (variable in stateMachine.variables.values) {
            variableStates[variable.name] = variable.initialValue
        }

        // Initialize input states
        for (input in stateMachine.inputs.values) {
            inputStates[input.name] = Array(initialSize(input)) { LogicState.EMPTY }
        }

        // Initialize output states
        for (output in stateMachine.outputs.values) {
            outputStates[output.name] = Array(initialSize(output)) { output.initialValue }
        }

        configure()
    }

    fun configure() {
        // Update size of inputs defined by parameters.
        for (input in stateMachine.inputs.values) {
            if (input.initialSize is StateMachineIOState.InitialSizeProvider.Parameter) {
                resize(input, initialSize(input))
            }
        }

        // Update size of outputs defined by parameters.
        for (output in stateMachine.outputs.values) {
            if (output.initialSize is StateMachineIOState.InitialSizeProvider.Parameter) {
                resize(output, initialSize(output))
            }
        }

        // Configure the state machine and publish configure event.
        stateMachine.configure(VireStateMachineConfigureContext(this))
        simulation.publish(StateMachineConfigureEvent(simulation, this))
    }

    fun initialSize(stateVariable: StateMachineIOState): Int {
        return when (val provider = stateVariable.initialSize) {
            is StateMachineIOState.InitialSizeProvider.Value -> provider.value
            is StateMachineIOState.InitialSizeProvider.Parameter -> this[provider.parameter].toInt()
        }
    }

    fun initializeInputs() {
        // Initialize input states
        for (input in stateMachine.inputs.values) {
            Arrays.setAll(inputStates[input.name]) { LogicState.EMPTY }
        }
    }

    fun update() {
        stateMachine.update(VireStateMachineUpdateContext(this))
    }

    fun initializeOutputs() {
        // Initialize input states
        for (output in stateMachine.outputs.values) {
            Arrays.setAll(outputStates[output.name]) { LogicState.EMPTY }
        }
    }

    fun pullInput(input: StateMachineInput, index: Int, logicState: LogicState) {
        inputStates[input.name]!![index] = LogicState.merge(inputStates[input.name]!![index], logicState)
    }

    fun pushOutput(output: StateMachineOutput, index: Int, logicState: LogicState): LogicState {
        return LogicState.merge(logicState, outputStates[output.name]!![index])
    }

    override fun has(name: String): Boolean {
        return name in stateMachine.stateVariableNames
    }

    override fun size(name: String): Int {
        return when (name) {
            in stateMachine.inputs -> {
                inputStates[name]!!.size
            }
            in stateMachine.outputs -> {
                outputStates[name]!!.size
            }
            else -> {
                throw IllegalArgumentException("No input or output with the name \"$name\" exists on the state machine.")
            }
        }
    }

    fun resize(name: String, size: Int) {
        when (name) {
            in stateMachine.inputs -> {
                resize(stateMachine.inputs[name]!!, size)
            }
            in stateMachine.outputs -> {
                resize(stateMachine.outputs[name]!!, size)
            }
            else -> {
                throw IllegalArgumentException("No input or output with the name \"$name\" exists on the state machine.")
            }
        }
    }

    override fun typeOf(name: String): KType {
        return when (name) {
            in stateMachine.parameters -> stateMachine.parameters[name]!!.type
            in stateMachine.variables -> stateMachine.variables[name]!!.type
            in stateMachine.inputs, in stateMachine.outputs -> kotlin.reflect.typeOf<LogicState>()
            else -> throw IllegalArgumentException("No state variable with the name \"$name\" exists on the state machine.")
        }
    }

    override fun get(name: String): Any? {
        return when (name) {
            in stateMachine.parameters -> parameterStates[name]
            in stateMachine.variables -> variableStates[name]
            in stateMachine.inputs -> inputStates[name]!![0]
            in stateMachine.outputs -> outputStates[name]!![0]
            else -> throw IllegalArgumentException("No state variable with the name \"$name\" exists on the state machine.")
        }
    }

    override fun get(name: String, index: Int): LogicState {
        return when (name) {
            in stateMachine.inputs -> inputStates[name]!![index]
            in stateMachine.outputs -> outputStates[name]!![index]
            else -> throw IllegalArgumentException("No io state variable with the name \"$name\" exists on the state machine.")
        }
    }

    override fun vector(name: String): Array<LogicState> {
        return when (name) {
            in stateMachine.inputs -> inputStates[name]!!
            in stateMachine.outputs -> outputStates[name]!!
            else -> throw IllegalArgumentException("No io state variable with the name \"$name\" exists on the state machine.")
        }
    }

    operator fun set(name: String, value: Any?) {
        when (name) {
            in stateMachine.parameters -> {
                require(stateMachine.parameters[name]!!.isValidTypeAndValue(value)) { "Invalid value $value for parameter \"$name\"." }
                parameterStates[name] = value
            }
            in stateMachine.variables -> {
                require(stateMachine.variables[name]!!.isValidType(value)) { "Invalid value $value for variable \"$name\"" }
                variableStates[name] = value
            }
            in stateMachine.inputs -> {
                require(value is LogicState)
                inputStates[name]!![0] = value
            }
            in stateMachine.outputs -> {
                require(value is LogicState)
                outputStates[name]!![0] = value
            }
            else -> throw IllegalArgumentException("No state variable with the name \"$name\" exists on the state machine.")
        }
    }

    operator fun set(name: String, index: Int, value: LogicState) {
        when (name) {
            in stateMachine.inputs -> inputStates[name]!![index] = value
            in stateMachine.outputs -> outputStates[name]!![index] = value
            else -> throw IllegalArgumentException("No io state variable with the name \"$name\" exists on the state machine.")
        }
    }

    fun vector(name: String, value: Array<LogicState>) {
        when (name) {
            in stateMachine.inputs -> inputStates[name] = value
            in stateMachine.outputs -> outputStates[name] = value
            else -> throw IllegalArgumentException("No io state variable with the name \"$name\" exists on the state machine.")
        }
    }

    override fun size(input: StateMachineInput): Int {
        return inputStates[input.name]!!.size
    }

    override fun size(output: StateMachineOutput): Int {
        return outputStates[output.name]!!.size
    }

    fun resize(input: StateMachineInput, size: Int) {
        val previous = inputStates[input.name]!!
        inputStates[input.name] = Array(size) { index ->
            if (index < previous.size) {
                previous[index]
            } else {
                LogicState.EMPTY
            }
        }
    }

    fun resize(output: StateMachineOutput, size: Int) {
        val previous = outputStates[output.name]!!
        outputStates[output.name] = Array(size) { index ->
            if (index < previous.size) {
                previous[index]
            } else {
                output.initialValue
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameter: StateMachineParameter<T>): T {
        return parameterStates[parameter.name] as T
    }

    operator fun <T> set(parameter: StateMachineParameter<T>, value: T) {
        // Check that the parameter is valid.
        require(parameter.isValid(value))

        parameterStates[parameter.name] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(variable: StateMachineVariable<T>): T {
        return variableStates[variable.name]!! as T
    }

    override fun <T> set(variable: StateMachineVariable<T>, value: T) {
        variableStates[variable.name] = value
    }

    override fun get(input: StateMachineInput, index: Int): LogicState {
        return inputStates[input.name]!![index]
    }

    override fun vector(input: StateMachineInput): Array<LogicState> {
        return inputStates[input.name]!!
    }

    operator fun set(input: StateMachineInput, index: Int = 0, value: LogicState) {
        inputStates[input.name]!![index] = value
    }

    fun vector(input: StateMachineInput, value: Array<LogicState>) {
        require(value.size == size(input)) { "Invalid vector size." }
        inputStates[input.name] = value
    }

    override fun get(output: StateMachineOutput, index: Int): LogicState {
        return outputStates[output.name]!![index]
    }

    override fun vector(output: StateMachineOutput): Array<LogicState> {
        return outputStates[output.name]!!
    }

    operator fun set(output: StateMachineOutput, index: Int = 0, value: LogicState) {
        outputStates[output.name]!![index] = value
    }

    fun vector(output: StateMachineOutput, value: Array<LogicState>) {
        require(value.size == size(output)) { "Invalid vector size." }
        outputStates[output.name] = value
    }

    override fun configureParameters(action: StateMachineInstance.ConfigurationContext.() -> Unit): Boolean {
        val previous = parameterStates.toMutableMap()
        try {
            // Apply the action.
            VireConfigurationContext(this).action()
        } catch (exception: Exception) {
            // Reset to previous values.
            parameterStates.putAll(previous)
            return false
        }

        configure()
        return true
    }

    class VireConfigurationContext(
        val instance: VireStateMachineInstance,
    ) : StateMachineInstance.ConfigurationContext {

        override val stateMachine: StateMachine
            get() = instance.stateMachine

        override fun <T> get(parameter: StateMachineParameter<T>): T {
            return instance[parameter]
        }

        override fun <T> set(parameter: StateMachineParameter<T>, value: T) {
            instance[parameter] = value
        }

        override fun get(parameterName: String): Any? {
            return instance[parameterName]
        }

        override fun set(parameterName: String, value: Any?) {
            instance[parameterName] = value
        }
    }

    class VireInitialConfigurationContext(
        override val stateMachine: StateMachine,
        private val initialParameterStates: MutableMap<String, Any?>,
    ) : StateMachineInstance.ConfigurationContext {

        @Suppress("UNCHECKED_CAST")
        override fun <T> get(parameter: StateMachineParameter<T>): T {
            return initialParameterStates[parameter.name] as T
        }

        override fun <T> set(parameter: StateMachineParameter<T>, value: T) {
            // Check that the parameter is valid.
            require(parameter.isValid(value)) { "Value $value does not meet the requirements for the parameter ${parameter.name}" }

            initialParameterStates[parameter.name] = value
        }

        override fun get(parameterName: String): Any? {
            require(parameterName in stateMachine.parameters)
            return initialParameterStates[parameterName]
        }

        override fun set(parameterName: String, value: Any?) {
            require(parameterName in stateMachine.parameters)
            val parameter = stateMachine.parameters[parameterName]!!
            require(parameter.isValidTypeAndValue(value)) { "Invalid value $value for parameter \"${parameter.name}\"" }
            initialParameterStates[parameterName] = value
        }
    }
}
