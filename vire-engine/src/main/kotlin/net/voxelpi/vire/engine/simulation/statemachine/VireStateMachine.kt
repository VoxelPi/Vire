package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineConfigureContext
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInstance
import net.voxelpi.vire.api.simulation.statemachine.StateMachineOutput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineParameter
import net.voxelpi.vire.api.simulation.statemachine.StateMachineUpdateContext
import net.voxelpi.vire.api.simulation.statemachine.StateMachineVariable
import net.voxelpi.vire.api.simulation.statemachine.annotation.StateMachineTemplate
import net.voxelpi.vire.engine.VireImplementation
import kotlin.reflect.KClass

class VireStateMachine(
    override val id: Identifier,
    override val tags: Set<Identifier>,
    override val stateVariableNames: Set<String>,
    override val parameters: Map<String, StateMachineParameter<*>>,
    override val variables: Map<String, StateMachineVariable<*>>,
    override val inputs: Map<String, StateMachineInput>,
    override val outputs: Map<String, StateMachineOutput>,
    override val configure: (StateMachineConfigureContext) -> Unit,
    override val update: (StateMachineUpdateContext) -> Unit,
) : StateMachine {

    override fun createInstance(
        configuration: StateMachineInstance.ConfigurationContext.() -> Unit,
    ): VireStateMachineInstance {
        return VireStateMachineInstance(this, configuration)
    }

    override fun createInstance(
        configuration: Map<String, Any?>,
    ): VireStateMachineInstance {
        return VireStateMachineInstance(this) {
            // Check that only existing parameters are specified.
            require(configuration.all { it.key in stateMachine.parameters.keys }) { "Unknown parameter specified" }

            // Apply configured values.
            for (parameter in stateMachine.parameters.values) {
                // Skip if no value is specified for the parameter.
                if (parameter.name !in configuration) {
                    continue
                }
                val configurationValue = configuration[parameter.name]

                // Check that the value is the right type.
                require(parameter.isValidType(configurationValue)) { "Invalid value specified for parameter '${parameter.name}'" }

                // Set the parameter.
                @Suppress("UNCHECKED_CAST")
                this[parameter as StateMachineParameter<Any?>] = configurationValue
            }
        }
    }

    class Builder(
        override val id: Identifier,
    ) : StateMachine.Builder() {

        override val tags: MutableSet<Identifier> = mutableSetOf()
        private val names: MutableSet<String> = mutableSetOf()
        private val parameters: MutableMap<String, StateMachineParameter<*>> = mutableMapOf()
        private val variables: MutableMap<String, StateMachineVariable<*>> = mutableMapOf()
        private val inputs: MutableMap<String, StateMachineInput> = mutableMapOf()
        private val outputs: MutableMap<String, StateMachineOutput> = mutableMapOf()

        override var configure: (StateMachineConfigureContext) -> Unit = {}

        override var update: (StateMachineUpdateContext) -> Unit = {}

        override fun <T, U : StateMachineParameter<T>> declare(parameter: U): U {
            require(parameter.name !in names) { "A parameter with the name \"${parameter.name}\" already exists." }
            names.add(parameter.name)
            parameters[parameter.name] = parameter
            return parameter
        }

        override fun <T> declare(variable: StateMachineVariable<T>): StateMachineVariable<T> {
            require(variable.name !in names) { "A variable with the name \"${variable.name}\" already exists." }
            names.add(variable.name)
            variables[variable.name] = variable
            return variable
        }

        override fun declare(input: StateMachineInput): StateMachineInput {
            require(input.name !in names) { "A input with the name \"${input.name}\" already exists." }
            names.add(input.name)
            inputs[input.name] = input
            return input
        }

        override fun declare(output: StateMachineOutput): StateMachineOutput {
            require(output.name !in names) { "A output with the name \"${output.name}\" already exists." }
            names.add(output.name)
            outputs[output.name] = output
            return output
        }

        override fun create(): VireStateMachine {
            return VireStateMachine(
                id,
                tags,
                names,
                parameters,
                variables,
                inputs,
                outputs,
                configure,
                update,
            )
        }
    }

    companion object {

        /**
         * Creates a new state machine.
         */
        fun create(id: Identifier, init: StateMachine.Builder.() -> Unit): VireStateMachine {
            return VireImplementation.stateMachineFactory.create(id, init)
        }

        /**
         * Generates a new state machine from the given template [type].
         */
        fun generate(type: KClass<out StateMachineTemplate>): VireStateMachine {
            return VireImplementation.stateMachineFactory.generate(type)
        }

        /**
         * Generates a new state machine from the given template [T].
         */
        inline fun <reified T : StateMachineTemplate> generate(): VireStateMachine {
            return VireImplementation.stateMachineFactory.generate(T::class)
        }
    }
}
