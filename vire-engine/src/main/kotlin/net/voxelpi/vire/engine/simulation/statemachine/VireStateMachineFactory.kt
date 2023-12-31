package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineFactory
import net.voxelpi.vire.api.simulation.statemachine.StateMachineIOState
import net.voxelpi.vire.api.simulation.statemachine.StateMachineParameter
import net.voxelpi.vire.api.simulation.statemachine.annotation.ByteLimits
import net.voxelpi.vire.api.simulation.statemachine.annotation.DoubleLimits
import net.voxelpi.vire.api.simulation.statemachine.annotation.FloatLimits
import net.voxelpi.vire.api.simulation.statemachine.annotation.InitialParameterSize
import net.voxelpi.vire.api.simulation.statemachine.annotation.InitialSize
import net.voxelpi.vire.api.simulation.statemachine.annotation.Input
import net.voxelpi.vire.api.simulation.statemachine.annotation.IntLimits
import net.voxelpi.vire.api.simulation.statemachine.annotation.LongLimits
import net.voxelpi.vire.api.simulation.statemachine.annotation.Output
import net.voxelpi.vire.api.simulation.statemachine.annotation.Parameter
import net.voxelpi.vire.api.simulation.statemachine.annotation.ShortLimits
import net.voxelpi.vire.api.simulation.statemachine.annotation.StateMachineMeta
import net.voxelpi.vire.api.simulation.statemachine.annotation.StateMachineTemplate
import net.voxelpi.vire.api.simulation.statemachine.annotation.StringSelection
import net.voxelpi.vire.api.simulation.statemachine.annotation.UByteLimits
import net.voxelpi.vire.api.simulation.statemachine.annotation.UIntLimits
import net.voxelpi.vire.api.simulation.statemachine.annotation.ULongLimits
import net.voxelpi.vire.api.simulation.statemachine.annotation.UShortLimits
import net.voxelpi.vire.api.simulation.statemachine.annotation.Variable
import net.voxelpi.vire.api.simulation.statemachine.input
import net.voxelpi.vire.api.simulation.statemachine.output
import net.voxelpi.vire.api.simulation.statemachine.parameter
import net.voxelpi.vire.api.simulation.statemachine.variable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

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
    override fun generate(
        type: KClass<out StateMachineTemplate>,
    ): StateMachine {
        // Get state machine meta.
        val meta = type.findAnnotation<StateMachineMeta>()
        require(meta != null) { "State machine template must be annotated with the StateMachineMeta annotation." }
        val id = Identifier(meta.namespace, meta.id)

        // Get all annotated properties.
        val inputProperties = type.memberProperties
            .filter { it.findAnnotation<Input>() != null }
            .filter { it.returnType.jvmErasure == LogicState::class || it.returnType == typeOf<Array<LogicState>>() }
            .filterIsInstance<KMutableProperty1<StateMachineTemplate, *>>()
            .associateBy { it.findAnnotation<Input>()!!.id }

        val outputProperties = type.memberProperties
            .filter { it.findAnnotation<Output>() != null }
            .filter { it.returnType.jvmErasure == LogicState::class || it.returnType == typeOf<Array<LogicState>>() }
            .filterIsInstance<KProperty1<StateMachineTemplate, *>>()
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
                val value = property.get(testTemplateInstance)
                if (value is Array<*>) {
                    value[0] as LogicState
                } else {
                    value as LogicState
                }
            }
            Pair(id, initialValue)
        }.associate { it }
        val variableInitialValues = variableProperties.map { (id, property) ->
            Pair(id, property.get(testTemplateInstance))
        }.associate { it }
        val parameterInitialValues = parameterProperties.map { (id, property) ->
            Pair(id, property.get(testTemplateInstance))
        }.associate { it }

        // Create parameters, variables, inputs and outputs.
        val parameters = parameterProperties.map { (id, property) ->
            // Get the initial value of the parameter.
            val initialValue = parameterInitialValues[id]

            // Check the parameter constraints.
            when {
                property.findAnnotation<StringSelection>() != null -> {
                    require(initialValue is String) { "A property annotated by StringSelection must be a String." }
                    val predicateAnnotation = property.findAnnotation<StringSelection>()!!
                    parameter(id, property.returnType, initialValue, predicateAnnotation.values.toList())
                }
                property.findAnnotation<ByteLimits>() != null -> {
                    require(initialValue is Byte) { "A property annotated by ByteLimits must be a Byte." }
                    val predicateAnnotation = property.findAnnotation<ByteLimits>()!!
                    parameter(id, initialValue, predicateAnnotation.min, predicateAnnotation.max) as StateMachineParameter<Any?>
                }
                property.findAnnotation<UByteLimits>() != null -> {
                    require(initialValue is UByte) { "A property annotated by UByteLimits must be an UByte." }
                    val predicateAnnotation = property.findAnnotation<UByteLimits>()!!
                    parameter(id, initialValue, predicateAnnotation.min, predicateAnnotation.max)
                }
                property.findAnnotation<ShortLimits>() != null -> {
                    require(initialValue is Short) { "A property annotated by ShortLimits must be a Short." }
                    val predicateAnnotation = property.findAnnotation<ShortLimits>()!!
                    parameter(id, initialValue, predicateAnnotation.min, predicateAnnotation.max)
                }
                property.findAnnotation<UShortLimits>() != null -> {
                    require(initialValue is UShort) { "A property annotated by UShortLimits must be an UShort." }
                    val predicateAnnotation = property.findAnnotation<UShortLimits>()!!
                    parameter(id, initialValue, predicateAnnotation.min, predicateAnnotation.max)
                }
                property.findAnnotation<IntLimits>() != null -> {
                    require(initialValue is Int) { "A property annotated by IntLimits must be an Int." }
                    val predicateAnnotation = property.findAnnotation<IntLimits>()!!
                    parameter(id, initialValue, predicateAnnotation.min, predicateAnnotation.max)
                }
                property.findAnnotation<UIntLimits>() != null -> {
                    require(initialValue is UInt) { "A property annotated by UIntLimits must be an UInt." }
                    val predicateAnnotation = property.findAnnotation<UIntLimits>()!!
                    parameter(id, initialValue, predicateAnnotation.min, predicateAnnotation.max)
                }
                property.findAnnotation<LongLimits>() != null -> {
                    require(initialValue is Long) { "A property annotated by LongLimits must be a Long." }
                    val predicateAnnotation = property.findAnnotation<LongLimits>()!!
                    parameter(id, initialValue, predicateAnnotation.min, predicateAnnotation.max)
                }
                property.findAnnotation<ULongLimits>() != null -> {
                    require(initialValue is ULong) { "A property annotated by ULongLimits must be an ULong." }
                    val predicateAnnotation = property.findAnnotation<ULongLimits>()!!
                    parameter(id, initialValue, predicateAnnotation.min, predicateAnnotation.max)
                }
                property.findAnnotation<FloatLimits>() != null -> {
                    require(initialValue is Float) { "A property annotated by FloatLimits must be a Float." }
                    val predicateAnnotation = property.findAnnotation<FloatLimits>()!!
                    parameter(id, initialValue, predicateAnnotation.min, predicateAnnotation.max)
                }
                property.findAnnotation<DoubleLimits>() != null -> {
                    require(initialValue is Double) { "A property annotated by DoubleLimits must be a Double." }
                    val predicateAnnotation = property.findAnnotation<DoubleLimits>()!!
                    parameter(id, initialValue, predicateAnnotation.min, predicateAnnotation.max)
                }
                else -> {
                    // Fallback to unconstrained parameter.
                    parameter(id, property.returnType, initialValue)
                }
            }
        }.map { it as StateMachineParameter<Any?> }
        val variables = variableProperties.map { (id, property) ->
            variable(id, property.returnType, variableInitialValues[id])
        }
        val inputs = inputProperties.map { (id, property) ->
            val initialSize = when {
                property.findAnnotation<InitialSize>() != null -> {
                    StateMachineIOState.InitialSizeProvider.Value(property.findAnnotation<InitialSize>()!!.size)
                }
                property.findAnnotation<InitialParameterSize>() != null -> {
                    val parameterId = property.findAnnotation<InitialParameterSize>()!!.parameter
                    require(parameterId in parameterProperties) {
                        "Unknown parameter \"$parameterId\" used for initial size of input \"$id\""
                    }
                    val parameter = parameters.first { it.name == parameterId }
                    require(parameter.type.isSubtypeOf(typeOf<Number>())) {
                        "Non numeric parameter \"$parameterId\" (${parameter.type}) used for initial size of input \"$id\""
                    }
                    parameter as StateMachineParameter<out Number>
                    StateMachineIOState.InitialSizeProvider.Parameter(parameter)
                }
                else -> {
                    StateMachineIOState.InitialSizeProvider.Value(1)
                }
            }
            // Property must be an array if the initial size is greater than 1 or a parameter.
            if (!(initialSize is StateMachineIOState.InitialSizeProvider.Value && initialSize.value == 1)) {
                require(property.returnType.isSubtypeOf(typeOf<Array<LogicState>>())) {
                    "Input property \"$id\" (${property.returnType.classifier}) must be an array. (size > 1 or unknown)"
                }
            }

            input(id, initialSize = initialSize)
        }
        val outputs = outputProperties.map { (id, property) ->
            val initialSize = when {
                !property.isLateinit -> {
                    StateMachineIOState.InitialSizeProvider.Value(outputInitialValues[id]!!.size)
                }
                property.findAnnotation<InitialSize>() != null -> {
                    StateMachineIOState.InitialSizeProvider.Value(property.findAnnotation<InitialSize>()!!.size)
                }
                property.findAnnotation<InitialParameterSize>() != null -> {
                    val parameterId = property.findAnnotation<InitialParameterSize>()!!.parameter
                    require(parameterId in parameterProperties) {
                        "Unknown parameter \"$parameterId\" used for initial size of output \"$id\""
                    }
                    val parameter = parameters.first { it.name == parameterId }
                    require(parameter.type.isSubtypeOf(typeOf<Number>())) {
                        "Non numeric parameter \"$parameterId\" (${parameter.type}) used for initial size of output \"$id\""
                    }
                    parameter as StateMachineParameter<out Number>
                    StateMachineIOState.InitialSizeProvider.Parameter(parameter)
                }
                else -> {
                    StateMachineIOState.InitialSizeProvider.Value(1)
                }
            }
            // Property must be an array if the initial size is greater than 1 or a parameter.
            if (!(initialSize is StateMachineIOState.InitialSizeProvider.Value && initialSize.value == 1)) {
                require(property.returnType.isSubtypeOf(typeOf<Array<LogicState>>())) {
                    "Output property \"$id\" (${property.returnType.classifier}) must be an array. (size > 1 or unknown)"
                }
            }

            output(id, initialSize = initialSize, initialValue = outputInitialValues[id]!!)
        }
        val templateInstance = variable("__template_instance__", testTemplateInstance)

        val stateMachine = create(id) {
            // Declare all created state variables.
            parameters.forEach(this::declare)
            variables.forEach(this::declare)
            inputs.forEach(this::declare)
            outputs.forEach(this::declare)

            configure = { context ->
                val instance = type.createInstance()
                context[templateInstance] = instance

                // Update all parameters that may have been configured.
                for (parameter in parameters) {
                    val property = parameterProperties[parameter.name]!! as KMutableProperty1<StateMachineTemplate, Any?>
                    property.set(instance, context[parameter])
                }

                // Create all late init input arrays.
                for (input in inputs) {
                    val property = inputProperties[input.name]!!
                    if (property.isLateinit) {
                        if (property.returnType.isSubtypeOf(typeOf<Array<LogicState>>())) {
                            (property as KMutableProperty1<StateMachineTemplate, Array<LogicState>>)
                                .set(instance, Array(context.size(input)) { LogicState.EMPTY })
                        }
                    }
                }

                // Create all late init output arrays.
                for (output in outputs) {
                    val property = outputProperties[output.name]!!
                    if (property.isLateinit) {
                        if (property.returnType.isSubtypeOf(typeOf<Array<LogicState>>())) {
                            (property as KMutableProperty1<StateMachineTemplate, Array<LogicState>>)
                                .set(instance, Array(context.size(output)) { output.initialValue })
                        }
                    }
                }

                // Run the configure method on the template.
                instance.configure()
            }

            update = { context ->
                // Get template instance.
                val instance = context[templateInstance]

                // Update all inputs.
                for (input in inputs) {
                    val property = inputProperties[input.name]!!
                    if (property.returnType.isSubtypeOf(typeOf<Array<LogicState>>())) {
                        (property as KMutableProperty1<StateMachineTemplate, Array<LogicState>>).set(instance, context.vector(input))
                    } else {
                        (property as KMutableProperty1<StateMachineTemplate, LogicState>).set(instance, context[input])
                    }
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
                    if (property.returnType.isSubtypeOf(typeOf<Array<LogicState>>())) {
                        context.vector(output, (property as KProperty1<StateMachineTemplate, Array<LogicState>>).get(instance))
                    } else {
                        context[output] = (property as KProperty1<StateMachineTemplate, LogicState>).get(instance)
                    }
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
