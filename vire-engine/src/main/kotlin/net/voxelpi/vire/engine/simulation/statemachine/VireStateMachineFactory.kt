package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineFactory
import net.voxelpi.vire.api.simulation.statemachine.StateMachineIOState
import net.voxelpi.vire.api.simulation.statemachine.annotation.Input
import net.voxelpi.vire.api.simulation.statemachine.annotation.Output
import net.voxelpi.vire.api.simulation.statemachine.annotation.Parameter
import net.voxelpi.vire.api.simulation.statemachine.annotation.StateMachineMeta
import net.voxelpi.vire.api.simulation.statemachine.annotation.StateMachineTemplate
import net.voxelpi.vire.api.simulation.statemachine.annotation.Variable
import net.voxelpi.vire.api.simulation.statemachine.input
import net.voxelpi.vire.api.simulation.statemachine.output
import net.voxelpi.vire.api.simulation.statemachine.variable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

class VireStateMachineFactory : StateMachineFactory {

    override fun create(
        id: Identifier,
        init: StateMachine.Builder.() -> Unit,
    ): VireStateMachine {
        val builder = VireStateMachine.Builder(id)
        builder.init()
        return builder.create()
    }

    @Suppress("UNCHECKED_CAST")
    override fun create(
        type: KClass<out StateMachineTemplate>,
    ): StateMachine {
        // Get state machine meta.
        val meta = type.findAnnotation<StateMachineMeta>()
        require(meta != null) { "State machine template must be annotated with the StateMachineMeta annotation." }
        val id = Identifier(meta.namespace, meta.id)

        // Get all annotated properties.
        val inputProperties = type.memberProperties
            .filter { it.findAnnotation<Input>() != null }
            .filter { it.returnType.jvmErasure == LogicState::class }
            .filterIsInstance<KMutableProperty1<StateMachineTemplate, LogicState>>()
            .associateBy { it.findAnnotation<Input>()!!.id }

        val outputProperties = type.memberProperties
            .filter { it.findAnnotation<Output>() != null }
            .filter { it.returnType.jvmErasure == LogicState::class }
            .filterIsInstance<KProperty1<StateMachineTemplate, LogicState>>()
            .associateBy { it.findAnnotation<Output>()!!.id }

        val variableProperties = type.memberProperties
            .filter { it.findAnnotation<Variable>() != null }
            .filterIsInstance<KMutableProperty1<StateMachineTemplate, *>>()
            .associateBy { it.findAnnotation<Variable>()!!.id }

        val parameterProperties = type.memberProperties
            .filter { it.findAnnotation<Parameter>() != null }
            .filterIsInstance<KMutableProperty1<StateMachineTemplate, *>>()
            .associateBy { it.findAnnotation<Parameter>()!!.id }

        // Get initial values of all parameters, variables and outputs.
        val testTemplateInstance = type.createInstance()
        val outputInitialValues = outputProperties.map { (id, property) ->
            val initialValue = if (property is KMutableProperty1 && property.isLateinit) {
                LogicState.EMPTY
            } else {
                property.get(testTemplateInstance)
            }
            Pair(id, initialValue)
        }.associate { it }
        val variableInitialValues = variableProperties.map { (id, property) ->
            Pair(id, property.get(testTemplateInstance))
        }.associate { it }

        // Create variables, parameters inputs and outputs.
        val variables = variableProperties.map { (id, property) ->
            variable(id, property.returnType, variableInitialValues[id])
        }
        val inputs = inputProperties.map { (id, _) ->
            input(id, initialSize = 1) // TODO: Initial size?
        }
        val outputs = outputProperties.map { (id, _) ->
            output(id, initialSize = 1, initialValue = outputInitialValues[id]!!) // TODO: Initial size?
        }
        val templateInstance = variable("__template_instance__", testTemplateInstance)

        val stateMachine = create(id) {
            // Declare all created state variables.
            for (variable in variables) {
                declare(variable)
            }
            for (input in inputs) {
                declare(input)
            }
            for (output in outputs) {
                declare(output)
            }

            configure = { context ->
                val instance = type.createInstance()
                context[templateInstance] = instance

                // Run the configure method on the template.
                instance.configure()
            }

            update = { context ->
                // Get template instance.
                val instance = context[templateInstance]

                // Update all inputs.
                for (input in inputs) {
                    val property = inputProperties[input.name]!!
                    property.set(instance, context[input])
                }
                for (variable in variables) {
                    val property = variableProperties[variable.name]!! as KMutableProperty1<StateMachineTemplate, Any?>
                    property.set(instance, context[variable])
                }

                // Run the configure method on the template.
                instance.update()

                // Update all outputs
                for (output in outputs) {
                    val property = outputProperties[output.name]!!
                    context[output] = property.get(instance)
                }
                for (variable in variables) {
                    val property = variableProperties[variable.name]!!
                    context[variable] = property.get(instance)
                }
            }
        }

        return stateMachine
    }

    override fun createInput(
        name: String,
        initialSize: StateMachineIOState.InitialSizeProvider,
    ): VireStateMachineInput {
        return VireStateMachineInput(name, initialSize)
    }

    override fun createOutput(
        name: String,
        initialSize: StateMachineIOState.InitialSizeProvider,
        initialValue: LogicState,
    ): VireStateMachineOutput {
        return VireStateMachineOutput(name, initialSize, initialValue)
    }

    override fun <T> createVariable(
        name: String,
        type: KType,
        initialValue: T,
    ): VireStateMachineVariable<T> {
        return VireStateMachineVariable(name, type, initialValue)
    }

    override fun <T> createUnconstrainedParameter(
        name: String,
        type: KType,
        initialValue: T,
    ): VireStateMachineParameter.Unconstrained<T> {
        return VireStateMachineParameter.Unconstrained(name, type, initialValue)
    }

    override fun <T> createPredicateParameter(
        name: String,
        type: KType,
        initialValue: T,
        predicate: (value: T) -> Boolean,
    ): VireStateMachineParameter.Predicate<T> {
        return VireStateMachineParameter.Predicate(name, type, initialValue, predicate)
    }

    override fun <T> createSelectionParameter(
        name: String,
        type: KType,
        initialValue: T,
        selection: Collection<T>,
    ): VireStateMachineParameter.Selection<T> {
        return VireStateMachineParameter.Selection(name, type, initialValue, selection)
    }

    override fun <T : Comparable<T>> createRangeParameter(
        name: String,
        type: KType,
        initialValue: T,
        min: T,
        max: T,
    ): VireStateMachineParameter.Range<T> {
        return VireStateMachineParameter.Range(name, type, initialValue, min, max)
    }
}
