package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineConfigureContext
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineOutput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineParameter
import net.voxelpi.vire.api.simulation.statemachine.StateMachineUpdateContext
import net.voxelpi.vire.api.simulation.statemachine.StateMachineVariable

class VireStateMachine(
    override val id: Identifier,
    override val parameters: Map<String, StateMachineParameter<*>>,
    override val variables: Map<String, StateMachineVariable<*>>,
    override val inputs: Map<String, StateMachineInput>,
    override val outputs: Map<String, StateMachineOutput>,
    override val configure: (StateMachineConfigureContext) -> Unit,
    override val update: (StateMachineUpdateContext) -> Unit,
) : StateMachine {

    class Builder(
        override val id: Identifier,
    ) : StateMachine.Builder() {

        private val parameters: MutableMap<String, StateMachineParameter<*>> = mutableMapOf()
        private val variables: MutableMap<String, StateMachineVariable<*>> = mutableMapOf()
        private val inputs: MutableMap<String, StateMachineInput> = mutableMapOf()
        private val outputs: MutableMap<String, StateMachineOutput> = mutableMapOf()

        override var configure: (StateMachineConfigureContext) -> Unit = {}

        override var update: (StateMachineUpdateContext) -> Unit = {}
        override fun <T, U : StateMachineParameter<T>> declare(parameter: U): U {
            require(parameter.name !in parameters) { "A parameter with the name \"${parameter.name}\" already exists." }
            parameters[parameter.name] = parameter
            return parameter
        }

        override fun <T> declare(variable: StateMachineVariable<T>): StateMachineVariable<T> {
            require(variable.name !in parameters) { "A variable with the name \"${variable.name}\" already exists." }
            variables[variable.name] = variable
            return variable
        }

        override fun declare(input: StateMachineInput): StateMachineInput {
            require(input.name !in parameters) { "A input with the name \"${input.name}\" already exists." }
            inputs[input.name] = input
            return input
        }

        override fun declare(output: StateMachineOutput): StateMachineOutput {
            require(output.name !in parameters) { "A output with the name \"${output.name}\" already exists." }
            outputs[output.name] = output
            return output
        }

        override fun create(): VireStateMachine {
            return VireStateMachine(
                id,
                parameters,
                variables,
                inputs,
                outputs,
                configure,
                update,
            )
        }
    }
}
