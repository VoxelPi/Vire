package net.voxelpi.vire.engine.kernel.generated

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.generated.constraint.ByteInterval
import net.voxelpi.vire.engine.kernel.generated.constraint.ByteMax
import net.voxelpi.vire.engine.kernel.generated.constraint.ByteMin
import net.voxelpi.vire.engine.kernel.generated.constraint.DoubleInterval
import net.voxelpi.vire.engine.kernel.generated.constraint.DoubleMax
import net.voxelpi.vire.engine.kernel.generated.constraint.DoubleMin
import net.voxelpi.vire.engine.kernel.generated.constraint.FloatInterval
import net.voxelpi.vire.engine.kernel.generated.constraint.FloatMax
import net.voxelpi.vire.engine.kernel.generated.constraint.FloatMin
import net.voxelpi.vire.engine.kernel.generated.constraint.IntInterval
import net.voxelpi.vire.engine.kernel.generated.constraint.IntMax
import net.voxelpi.vire.engine.kernel.generated.constraint.IntMin
import net.voxelpi.vire.engine.kernel.generated.constraint.LongInterval
import net.voxelpi.vire.engine.kernel.generated.constraint.LongMax
import net.voxelpi.vire.engine.kernel.generated.constraint.LongMin
import net.voxelpi.vire.engine.kernel.generated.constraint.ShortInterval
import net.voxelpi.vire.engine.kernel.generated.constraint.ShortMax
import net.voxelpi.vire.engine.kernel.generated.constraint.ShortMin
import net.voxelpi.vire.engine.kernel.generated.constraint.StringSelection
import net.voxelpi.vire.engine.kernel.generated.constraint.UByteInterval
import net.voxelpi.vire.engine.kernel.generated.constraint.UByteMax
import net.voxelpi.vire.engine.kernel.generated.constraint.UByteMin
import net.voxelpi.vire.engine.kernel.generated.constraint.UIntInterval
import net.voxelpi.vire.engine.kernel.generated.constraint.UIntMax
import net.voxelpi.vire.engine.kernel.generated.constraint.UIntMin
import net.voxelpi.vire.engine.kernel.generated.constraint.ULongInterval
import net.voxelpi.vire.engine.kernel.generated.constraint.ULongMax
import net.voxelpi.vire.engine.kernel.generated.constraint.ULongMin
import net.voxelpi.vire.engine.kernel.generated.constraint.UShortInterval
import net.voxelpi.vire.engine.kernel.generated.constraint.UShortMax
import net.voxelpi.vire.engine.kernel.generated.constraint.UShortMin
import net.voxelpi.vire.engine.kernel.generated.declaration.FieldDeclaration
import net.voxelpi.vire.engine.kernel.generated.declaration.InputDeclaration
import net.voxelpi.vire.engine.kernel.generated.declaration.OutputDeclaration
import net.voxelpi.vire.engine.kernel.generated.declaration.ParameterDeclaration
import net.voxelpi.vire.engine.kernel.generated.declaration.SettingDeclaration
import net.voxelpi.vire.engine.kernel.generated.size.ConstantSize
import net.voxelpi.vire.engine.kernel.generated.size.ParametricSize
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.AllVariableConstraintBuilder
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.VectorVariableSize
import net.voxelpi.vire.engine.kernel.variable.createField
import net.voxelpi.vire.engine.kernel.variable.createInput
import net.voxelpi.vire.engine.kernel.variable.createOutput
import net.voxelpi.vire.engine.kernel.variable.createParameter
import net.voxelpi.vire.engine.kernel.variable.createSetting
import net.voxelpi.vire.engine.kernel.variable.max
import net.voxelpi.vire.engine.kernel.variable.min
import net.voxelpi.vire.engine.kernel.variable.range
import net.voxelpi.vire.engine.util.partition
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.typeOf

public inline fun <reified T : GeneratedKernel> generateKernel(): Kernel = generateKernel(T::class)

public fun generateKernel(type: KClass<out GeneratedKernel>): Kernel = CustomKernelFactory.generateKernel(type)

internal object CustomKernelFactory {

    @Suppress("UNCHECKED_CAST")
    fun generateKernel(type: KClass<out GeneratedKernel>): Kernel {
        // Get kernel definition
        val definition = type.findAnnotation<KernelDefinition>()
            ?: throw IllegalArgumentException("Custom kernels must be annotated with the KernelDefinition annotation.")

        // Get all kernel tags.
        val tags = type.findAnnotations<Tagged>().map { it.tags.toList().map(Identifier::parse) }.flatten()

        // Get all kernel properties.
        val properties = type.findAnnotations<WithProperty>().associate { Identifier.parse(it.key) to it.value }

        // Get all variable properties.
        val parameterProperties = collectVariableMutableProperties<ParameterDeclaration>(type, ParameterDeclaration::name)
        val settingProperties = collectVariableMutableProperties<SettingDeclaration>(type, SettingDeclaration::name)
        val fieldProperties = collectVariableMutableProperties<FieldDeclaration>(type, FieldDeclaration::name)
        val inputProperties = collectVariableMutableProperties<InputDeclaration>(type, InputDeclaration::name)
        val (vectorInputProperties, scalarInputProperties) = inputProperties
            .partition { (_, property) ->
                val propertyType = property.returnType
                propertyType.isSubtypeOf(typeOf<Array<*>>())
            }
        val outputProperties = collectVariableProperties<OutputDeclaration>(type, OutputDeclaration::name)
        val (vectorOutputProperties, scalarOutputProperties) = outputProperties
            .partition { (_, property) ->
                val propertyType = property.returnType
                propertyType.isSubtypeOf(typeOf<Array<*>>())
            }

        // Generate instance to check initial values.
        val initializationInstance = type.createInstance()
        val parameterInitializations = parameterProperties.map { (name, property) ->
            name to property.get(initializationInstance)
        }.toMap()
        val settingInitializations = settingProperties.map { (name, property) ->
            name to property.get(initializationInstance)
        }.toMap()
        val fieldInitializations = fieldProperties.map { (name, property) ->
            name to property.get(initializationInstance)
        }.toMap()
        val scalarOutputInitializations = scalarOutputProperties.map { (name, property) ->
            if (property is KMutableProperty1 && property.isLateinit) {
                name to LogicState.EMPTY
            } else {
                name to property.get(initializationInstance) as LogicState
            }
        }.toMap()
        val vectorOutputInitializations = vectorOutputProperties.map { (name, property) ->
            if (property is KMutableProperty1 && property.isLateinit) {
                name to null
            } else {
                name to property.get(initializationInstance) as Array<LogicState>
            }
        }.toMap()

        // Create a field for the instance of the class.
        val instanceField = createField(GeneratedKernel.INSTANCE_FIELD_NAME, initialization = { type.createInstance() })

        val parameters = parameterProperties.map { (name, property) ->
            // Get the initialization of the parameter.
            val initialization = parameterInitializations[name]

            // Create the parameter.
            createParameter(name, property.returnType, initialization = { initialization }) { buildConstraints(property, this) }
        }

        // Collect vector sizes
        val vectorInputSizes = vectorInputProperties.map { (name, property) ->
            if (property.isLateinit) {
                property.findAnnotation<ConstantSize>()?.let { constantSize ->
                    return@map name to VectorVariableSize.Value(constantSize.size)
                }
                property.findAnnotation<ParametricSize>()?.let { parametricSize ->
                    val parameterName = parametricSize.parameter
                    val parameter = parameters.find { it.name == parameterName }
                        ?: throw IllegalArgumentException("Unknown parameter '$parameterName' used for size of input '${property.name}'")
                    require(parameter.type.isSubtypeOf(typeOf<Int>())) {
                        "Invalid parameter type used for size of input '${property.name}'"
                    }
                    return@map name to VectorVariableSize.Parameter(parameter as Parameter<Int>)
                }
                throw IllegalStateException("No vector size specified for input ${property.name}")
            } else {
                property.name to VectorVariableSize.Value((property.get(initializationInstance) as Array<*>).size)
            }
        }.toMap()
        val vectorOutputSizes = vectorOutputProperties.map { (name, property) ->
            if (property.isLateinit) {
                property.findAnnotation<ConstantSize>()?.let { constantSize ->
                    return@map name to VectorVariableSize.Value(constantSize.size)
                }
                property.findAnnotation<ParametricSize>()?.let { parametricSize ->
                    val parameterName = parametricSize.parameter
                    val parameter = parameters.find { it.name == parameterName }
                        ?: throw IllegalArgumentException("Unknown parameter '$parameterName' used for size of output '${property.name}'")
                    require(parameter.type.isSubtypeOf(typeOf<Int>())) {
                        "Invalid parameter type used for size of output '${property.name}'"
                    }
                    return@map name to VectorVariableSize.Parameter(parameter as Parameter<Int>)
                }
                throw IllegalStateException("No vector size specified for output ${property.name}")
            } else {
                property.name to VectorVariableSize.Value((property.get(initializationInstance) as Array<*>).size)
            }
        }.toMap()

        val settings = settingProperties.map { (name, property) ->
            // Get the initialization of the parameter.
            val initialization = settingInitializations[name]

            // Create the setting.
            createSetting(name, property.returnType, initialization = { initialization }) { buildConstraints(property, this) }
        }
        val fields = fieldProperties.map { (name, property) ->
            // Get the initialization of the parameter.
            val initialization = fieldInitializations[name]

            // Create the field.
            createField(name, property.returnType, initialization = { initialization })
        }
        val scalarInputs = scalarInputProperties.map { (name, _) ->
            // Create the scalar input.
            createInput(name)
        }
        val vectorInputs = vectorInputProperties.map { (name, _) ->
            // Get the size of the input vector.
            val size = vectorInputSizes[name]!!

            // Create the vector input.
            createInput(name, size)
        }
        val scalarOutputs = scalarOutputProperties.map { (name, _) ->
            // Create the scalar output.
            createOutput(name, scalarOutputInitializations[name]!!)
        }
        val vectorOutputs = vectorOutputProperties.map { (name, _) ->
            // Get the size of the input vector.
            val size = vectorOutputSizes[name]!!

            // Get the initialization of the output vector.
            val initialization = vectorOutputInitializations[name]

            // Create the vector input.
            createOutput(name, size, initialization = { initialization?.get(index) ?: LogicState.EMPTY })
        }

        return kernel {
            // Add tags.
            this.tags.addAll(tags)

            // Set properties.
            this.properties.putAll(properties)

            // Declare variables.
            parameters.forEach(this::declare)
            settings.forEach(this::declare)
            fields.forEach(this::declare)
            scalarInputs.forEach(this::declare)
            vectorInputs.forEach(this::declare)
            scalarOutputs.forEach(this::declare)
            vectorOutputs.forEach(this::declare)
            declare(instanceField)

            onInitialization { context ->
                val instance = context[instanceField]

                // Update all parameter properties.
                for (parameter in parameters) {
                    val property = parameterProperties[parameter.name]!!
                    property.set(instance, context[parameter])
                }

                // Update all setting properties.
                for (setting in settings) {
                    val property = settingProperties[setting.name]!!
                    property.set(instance, context[setting])
                }

                // Update all inputs that have not yet been initialized.
                for (input in scalarInputs) {
                    val property = scalarInputProperties[input.name]!! as KMutableProperty1<GeneratedKernel, LogicState>
                    if (!property.isLateinit) {
                        continue
                    }

                    // Set property.
                    property.set(instance, LogicState.EMPTY)
                }
                for (input in vectorInputs) {
                    val property = vectorInputProperties[input.name]!! as KMutableProperty1<GeneratedKernel, Array<LogicState>>
                    if (!property.isLateinit) {
                        continue
                    }

                    // Set property.
                    property.set(instance, Array(context.size(input)) { LogicState.EMPTY })
                }

                // Update all outputs that have not yet been initialized.
                for (output in scalarOutputs) {
                    val property = scalarOutputProperties[output.name]!! as KMutableProperty1<GeneratedKernel, LogicState>
                    if (!property.isLateinit) {
                        continue
                    }

                    // Set property.
                    property.set(instance, LogicState.EMPTY)
                }
                for (output in vectorOutputs) {
                    val property = vectorOutputProperties[output.name]!! as KMutableProperty1<GeneratedKernel, Array<LogicState>>
                    if (!property.isLateinit) {
                        continue
                    }

                    // Set property.
                    property.set(instance, Array(context.size(output)) { LogicState.EMPTY })
                }

                // Run the custom kernel initialization.
                instance.initialize()
            }

            onUpdate { context ->
                val instance = context[instanceField]

                // Update all field properties.
                for (field in fields) {
                    val property = fieldProperties[field.name]!!
                    property.set(instance, context[field])
                }

                // Update all scalar input properties
                for (input in scalarInputs) {
                    val property = scalarInputProperties[input.name]!! as KMutableProperty1<GeneratedKernel, LogicState>
                    property.set(instance, context[input])
                }

                // Update all vector input properties.
                for (input in vectorInputs) {
                    val property = vectorInputProperties[input.name]!! as KMutableProperty1<GeneratedKernel, Array<LogicState>>
                    property.set(instance, context[input])
                }

                // Run the custom kernel update.
                instance.update()

                // Update all field variables.
                for (field in fields) {
                    val property = fieldProperties[field.name]!!
                    context[field] = property.get(instance)
                }

                // Update all scalar output variables.
                for (output in scalarOutputs) {
                    val property = scalarOutputProperties[output.name]!! as KMutableProperty1<GeneratedKernel, LogicState>
                    context[output] = property.get(instance)
                }

                // Update all vector output variables.
                for (output in vectorOutputs) {
                    val property = vectorOutputProperties[output.name]!! as KMutableProperty1<GeneratedKernel, Array<LogicState>>
                    context[output] = property.get(instance)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified A : Annotation> collectVariableMutableProperties(
        type: KClass<out GeneratedKernel>,
        naming: (annotation: A) -> String,
    ): Map<String, KMutableProperty1<GeneratedKernel, Any?>> {
        return type.memberProperties
            .filter { it.findAnnotation<A>() != null }
            .filterIsInstance<KMutableProperty1<GeneratedKernel, *>>()
            .associateBy { naming(it.findAnnotation<A>()!!).ifEmpty { it.name } } as Map<String, KMutableProperty1<GeneratedKernel, Any?>>
    }

    private inline fun <reified A : Annotation> collectVariableProperties(
        type: KClass<out GeneratedKernel>,
        naming: (annotation: A) -> String,
    ): Map<String, KProperty1<GeneratedKernel, Any?>> {
        return type.memberProperties
            .filter { it.findAnnotation<A>() != null }
            .filterIsInstance<KProperty1<GeneratedKernel, *>>()
            .associateBy { naming(it.findAnnotation<A>()!!).ifEmpty { it.name } }
    }

    @Suppress("UNCHECKED_CAST")
    private fun buildConstraints(property: KProperty1<GeneratedKernel, *>, builder: AllVariableConstraintBuilder<Any?>) {
        when {
            property.returnType.isSubtypeOf(typeOf<String>()) -> {
                property.findAnnotation<StringSelection>()?.let {
                    builder.selection(it.values)
                }
            }
            property.returnType.isSubtypeOf(typeOf<Byte>()) -> {
                property.findAnnotation<ByteMin>()?.let {
                    (builder as AllVariableConstraintBuilder<Byte>).min(it.min)
                }
                property.findAnnotation<ByteMax>()?.let {
                    (builder as AllVariableConstraintBuilder<Byte>).max(it.max)
                }
                property.findAnnotation<ByteInterval>()?.let {
                    (builder as AllVariableConstraintBuilder<Byte>).range(it.min, it.max)
                }
            }
            property.returnType.isSubtypeOf(typeOf<UByte>()) -> {
                property.findAnnotation<UByteMin>()?.let {
                    (builder as AllVariableConstraintBuilder<UByte>).min(it.min)
                }
                property.findAnnotation<UByteMax>()?.let {
                    (builder as AllVariableConstraintBuilder<UByte>).max(it.max)
                }
                property.findAnnotation<UByteInterval>()?.let {
                    (builder as AllVariableConstraintBuilder<UByte>).range(it.min, it.max)
                }
            }
            property.returnType.isSubtypeOf(typeOf<Short>()) -> {
                property.findAnnotation<ShortMin>()?.let {
                    (builder as AllVariableConstraintBuilder<Short>).min(it.min)
                }
                property.findAnnotation<ShortMax>()?.let {
                    (builder as AllVariableConstraintBuilder<Short>).max(it.max)
                }
                property.findAnnotation<ShortInterval>()?.let {
                    (builder as AllVariableConstraintBuilder<Short>).range(it.min, it.max)
                }
            }
            property.returnType.isSubtypeOf(typeOf<UShort>()) -> {
                property.findAnnotation<UShortMin>()?.let {
                    (builder as AllVariableConstraintBuilder<UShort>).min(it.min)
                }
                property.findAnnotation<UShortMax>()?.let {
                    (builder as AllVariableConstraintBuilder<UShort>).max(it.max)
                }
                property.findAnnotation<UShortInterval>()?.let {
                    (builder as AllVariableConstraintBuilder<UShort>).range(it.min, it.max)
                }
            }
            property.returnType.isSubtypeOf(typeOf<Int>()) -> {
                property.findAnnotation<IntMin>()?.let {
                    (builder as AllVariableConstraintBuilder<Int>).min(it.min)
                }
                property.findAnnotation<IntMax>()?.let {
                    (builder as AllVariableConstraintBuilder<Int>).max(it.max)
                }
                property.findAnnotation<IntInterval>()?.let {
                    (builder as AllVariableConstraintBuilder<Int>).range(it.min, it.max)
                }
            }
            property.returnType.isSubtypeOf(typeOf<UInt>()) -> {
                property.findAnnotation<UIntMin>()?.let {
                    (builder as AllVariableConstraintBuilder<UInt>).min(it.min)
                }
                property.findAnnotation<UIntMax>()?.let {
                    (builder as AllVariableConstraintBuilder<UInt>).max(it.max)
                }
                property.findAnnotation<UIntInterval>()?.let {
                    (builder as AllVariableConstraintBuilder<UInt>).range(it.min, it.max)
                }
            }
            property.returnType.isSubtypeOf(typeOf<Long>()) -> {
                property.findAnnotation<LongMin>()?.let {
                    (builder as AllVariableConstraintBuilder<Long>).min(it.min)
                }
                property.findAnnotation<LongMax>()?.let {
                    (builder as AllVariableConstraintBuilder<Long>).max(it.max)
                }
                property.findAnnotation<LongInterval>()?.let {
                    (builder as AllVariableConstraintBuilder<Long>).range(it.min, it.max)
                }
            }
            property.returnType.isSubtypeOf(typeOf<ULong>()) -> {
                property.findAnnotation<ULongMin>()?.let {
                    (builder as AllVariableConstraintBuilder<ULong>).min(it.min)
                }
                property.findAnnotation<ULongMax>()?.let {
                    (builder as AllVariableConstraintBuilder<ULong>).max(it.max)
                }
                property.findAnnotation<ULongInterval>()?.let {
                    (builder as AllVariableConstraintBuilder<ULong>).range(it.min, it.max)
                }
            }
            property.returnType.isSubtypeOf(typeOf<Float>()) -> {
                property.findAnnotation<FloatMin>()?.let {
                    (builder as AllVariableConstraintBuilder<Float>).min(it.min)
                }
                property.findAnnotation<FloatMax>()?.let {
                    (builder as AllVariableConstraintBuilder<Float>).max(it.max)
                }
                property.findAnnotation<FloatInterval>()?.let {
                    (builder as AllVariableConstraintBuilder<Float>).range(it.min, it.max)
                }
            }
            property.returnType.isSubtypeOf(typeOf<Double>()) -> {
                property.findAnnotation<DoubleMin>()?.let {
                    (builder as AllVariableConstraintBuilder<Double>).min(it.min)
                }
                property.findAnnotation<DoubleMax>()?.let {
                    (builder as AllVariableConstraintBuilder<Double>).max(it.max)
                }
                property.findAnnotation<DoubleInterval>()?.let {
                    (builder as AllVariableConstraintBuilder<Double>).range(it.min, it.max)
                }
            }
        }
    }
}
