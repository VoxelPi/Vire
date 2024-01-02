package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.event.simulation.component.ComponentConfigureEvent
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineIOState
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInstance
import net.voxelpi.vire.api.simulation.statemachine.StateMachineOutput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineParameter
import net.voxelpi.vire.api.simulation.statemachine.StateMachineVariable
import net.voxelpi.vire.engine.simulation.VireSimulation
import net.voxelpi.vire.engine.simulation.component.VireComponent
import java.util.Arrays

class VireStateMachineInstance(
    private val component: VireComponent,
) : StateMachineInstance {

    override val stateMachine: StateMachine
        get() = component.stateMachine

    val simulation: VireSimulation
        get() = component.simulation

    private val parameterStates: MutableMap<String, Any?> = mutableMapOf()
    private val variableStates: MutableMap<String, Any?> = mutableMapOf()
    private val inputStates: MutableMap<String, Array<LogicState>> = mutableMapOf()
    private val outputStates: MutableMap<String, Array<LogicState>> = mutableMapOf()

    init {
        reset(true)
    }

    fun reset(parameters: Boolean) {
        if (parameters) {
            // Initialize parameter states
            for (parameter in stateMachine.parameters.values) {
                parameterStates[parameter.name] = parameter.initialValue
            }
        }

        // Initialize variable states
        for (variable in stateMachine.variables.values) {
            variableStates[variable.name] = variable.initialValue
        }

        // Initialize input states
        for (input in stateMachine.inputs.values) {
            inputStates[input.name] = Array(initialSize(input)) { LogicState.NONE }
        }

        // Initialize output states
        for (output in stateMachine.outputs.values) {
            outputStates[output.name] = Array(initialSize(output)) { LogicState.NONE }
        }

        // Configure the state machine.
        stateMachine.configure(VireStateMachineConfigureContext(this))

        // Publish event.
        simulation.publish(ComponentConfigureEvent(component))
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
            Arrays.setAll(inputStates[input.name]) { LogicState.NONE }
        }
    }

    fun update() {
        stateMachine.update(VireStateMachineUpdateContext(this))
    }

    fun initializeOutputs() {
        // Initialize input states
        for (output in stateMachine.outputs.values) {
            Arrays.setAll(outputStates[output.name]) { LogicState.NONE }
        }
    }

    fun pullInput(input: StateMachineInput, index: Int, logicState: LogicState) {
        inputStates[input.name]!![index] = LogicState.merge(inputStates[input.name]!![index], logicState)
    }

    fun pushOutput(output: StateMachineOutput, index: Int, logicState: LogicState): LogicState {
        return LogicState.merge(logicState, outputStates[output.name]!![index])
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
                LogicState.NONE
            }
        }
    }

    fun resize(output: StateMachineOutput, size: Int) {
        val previous = inputStates[output.name]!!
        outputStates[output.name] = Array(size) { index ->
            if (index < previous.size) {
                previous[index]
            } else {
                LogicState.NONE
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameter: StateMachineParameter<T>): T {
        return parameterStates[parameter.name]!! as T
    }

    operator fun <T> set(parameter: StateMachineParameter<T>, value: T) {
        // Check that the parameter is valid.
        require(parameter.isValid(value))

//        // Publish event. // TODO
//        simulation.publish(ComponentModifyParameterEvent(component, parameter, value, this[parameter])) // TODO

        parameterStates[parameter.name] = value
//        stateMachine.configure(this) // TODO
    }

    @Suppress("UNCHECKED_CAST")
    override  fun <T> get(variable: StateMachineVariable<T>): T {
        return variableStates[variable.name]!! as T
    }

    override fun <T> set(variable: StateMachineVariable<T>, value: T) {
        variableStates[variable.name] = value
    }

    override fun get(input: StateMachineInput, index: Int): LogicState {
        return inputStates[input.name]!![index]
    }

    operator fun set(input: StateMachineInput, index: Int, value: LogicState) {
        inputStates[input.name]!![index] = value
    }

    override fun get(output: StateMachineOutput, index: Int): LogicState {
        return outputStates[output.name]!![index]
    }

    operator fun set(output: StateMachineOutput, index: Int, value: LogicState) {
        outputStates[output.name]!![index] = value
    }

    override fun configureParameters(action: StateMachineInstance.ConfigurationContext.() -> Unit): Boolean {
        val previous = parameterStates.toMutableMap()
        try {
            VireConfigurationContext(this).action()
            return true
        } catch (exception: Exception) {
            // Reset to previous values.
            parameterStates.putAll(previous)
            return false
        }
    }

    class VireConfigurationContext(val instance: VireStateMachineInstance) : StateMachineInstance.ConfigurationContext {

        override fun <T> set(parameter: StateMachineParameter<T>, value: T) {
            instance[parameter] = value
        }
    }
}
