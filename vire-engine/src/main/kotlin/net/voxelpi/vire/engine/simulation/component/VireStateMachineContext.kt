package net.voxelpi.vire.engine.simulation.component

import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.api.simulation.component.StateMachineInput
import net.voxelpi.vire.api.simulation.component.StateMachineOutput
import net.voxelpi.vire.api.simulation.component.StateMachineParameter
import net.voxelpi.vire.api.simulation.component.StateMachineParameterContext
import net.voxelpi.vire.api.simulation.component.StateMachineVariable
import net.voxelpi.vire.api.simulation.event.simulation.component.ComponentConfigureEvent
import net.voxelpi.vire.api.simulation.event.simulation.component.ComponentModifyParameterEvent
import net.voxelpi.vire.api.simulation.network.NetworkState
import net.voxelpi.vire.engine.simulation.VireSimulation
import java.util.Arrays

class VireStateMachineContext(
    private val component: VireComponent,
) : StateMachineContext, StateMachineParameterContext {

    private val parameterStates: MutableMap<String, Any?> = mutableMapOf()
    private val variableStates: MutableMap<String, Any?> = mutableMapOf()
    private val inputStates: MutableMap<String, Array<NetworkState>> = mutableMapOf()
    private val outputStates: MutableMap<String, Array<NetworkState>> = mutableMapOf()

    override val stateMachine: StateMachine
        get() = component.stateMachine

    val simulation: VireSimulation
        get() = component.simulation

    init {
        reset(true)
    }

    fun reset(parameters: Boolean) {
        if (parameters) {
            // Initialize parameter states
            for (parameter in stateMachine.parameters()) {
                parameterStates[parameter.name] = parameter.initialValue
            }
        }

        // Initialize variable states
        for (variable in stateMachine.variables()) {
            variableStates[variable.name] = variable.initialValue
        }

        // Initialize input states
        for (input in stateMachine.inputs()) {
            inputStates[input.name] = Array(input.initialSize) { NetworkState.None }
        }

        // Initialize output states
        for (output in stateMachine.outputs()) {
            outputStates[output.name] = Array(output.initialSize) { output.initialValue }
        }

        // Configure the state machine.
        stateMachine.configure(this)

        // Publish event.
        simulation.publish(ComponentConfigureEvent(component))
    }

    override fun <T> get(parameter: StateMachineParameter<T>): T {
        @Suppress("UNCHECKED_CAST")
        return parameterStates[parameter.name]!! as T
    }

    operator fun <T> set(parameter: StateMachineParameter<T>, value: T) {
        // Check that the parameter is valid.
        require(parameter.isValid(value, this))

        // Publish event.
        simulation.publish(ComponentModifyParameterEvent(component, parameter, value, this[parameter]))

        parameterStates[parameter.name] = value
        stateMachine.configure(this)
    }

    override fun <T> get(variable: StateMachineVariable<T>): T {
        @Suppress("UNCHECKED_CAST")
        return variableStates[variable.name]!! as T
    }

    override fun <T> set(variable: StateMachineVariable<T>, value: T) {
        variableStates[variable.name] = value
    }

    override fun resize(input: StateMachineInput, size: Int) {
        val previous = inputStates[input.name]!!
        inputStates[input.name] = Array(size) { index ->
            if (index < previous.size) {
                previous[index]
            } else {
                NetworkState.None
            }
        }
    }

    override fun resize(output: StateMachineOutput, size: Int) {
        val previous = inputStates[output.name]!!
        inputStates[output.name] = Array(size) { index ->
            if (index < previous.size) {
                previous[index]
            } else {
                output.initialValue
            }
        }
    }

    override fun vector(input: StateMachineInput): Array<NetworkState> {
        return inputStates[input.name]!!
    }

    override fun get(input: StateMachineInput, index: Int): NetworkState {
        return this.vector(input)[index]
    }

    override fun vector(output: StateMachineOutput): Array<NetworkState> {
        return outputStates[output.name]!!
    }

    override fun get(output: StateMachineOutput, index: Int): NetworkState {
        return this.vector(output)[index]
    }

    override fun vector(output: StateMachineOutput, value: Array<NetworkState>) {
        value.copyInto(outputStates[output.name]!!)
    }

    override fun set(output: StateMachineOutput, index: Int, value: NetworkState) {
        outputStates[output.name]!![index] = value
    }

    override fun size(input: StateMachineInput): Int {
        return inputStates[input.name]!!.size
    }

    override fun size(output: StateMachineOutput): Int {
        return outputStates[output.name]!!.size
    }

    fun initializeInputs() {
        // Initialize input states
        for (input in stateMachine.inputs()) {
            Arrays.setAll(inputStates[input.name]) { NetworkState.None }
        }
    }

    fun initializeOutputs() {
        // Initialize input states
        for (output in stateMachine.outputs()) {
            Arrays.setAll(outputStates[output.name]) { NetworkState.None }
        }
    }

    fun pullInput(input: StateMachineInput, index: Int, networkState: NetworkState) {
        if (networkState == NetworkState.None) {
            return
        }

        if (networkState == NetworkState.Invalid) {
            inputStates[input.name]!![index] = NetworkState.Invalid
        }

        inputStates[input.name]!![index] = NetworkState.merge(inputStates[input.name]!![index], networkState)
    }

    fun pushOutput(output: StateMachineOutput, index: Int, networkState: NetworkState): NetworkState {
        return NetworkState.merge(networkState, outputStates[output.name]!![index])
    }
}
