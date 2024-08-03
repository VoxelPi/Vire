package net.voxelpi.vire.engine.kernel.procedual

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelConfigurationException
import net.voxelpi.vire.engine.kernel.KernelInitializationException
import net.voxelpi.vire.engine.kernel.KernelInstanceConfig
import net.voxelpi.vire.engine.kernel.KernelInstanceImpl
import net.voxelpi.vire.engine.kernel.KernelVariantBuilder
import net.voxelpi.vire.engine.kernel.KernelVariantBuilderImpl
import net.voxelpi.vire.engine.kernel.KernelVariantData
import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.MutableKernelState
import net.voxelpi.vire.engine.kernel.SpecializedKernel
import net.voxelpi.vire.engine.kernel.SpecializedKernelImpl
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.patch.MutableParameterStatePatch
import net.voxelpi.vire.engine.kernel.variable.patch.ParameterStatePatch
import net.voxelpi.vire.engine.kernel.variable.patch.generateInitialFieldStatePatch
import net.voxelpi.vire.engine.kernel.variable.patch.generateInitialOutputStatePatch
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialParameterStateProvider

public interface ProceduralKernel : Kernel {

    public val configurationAction: (ConfigurationContext) -> Unit

    public val initializationAction: (InitializationContext) -> Unit

    public val updateAction: (UpdateContext) -> Unit
}

internal class ProceduralKernelImpl(
    override val tags: Set<Identifier>,
    override val properties: Map<Identifier, String>,
    val variables: Map<String, Variable<*>>,
    override val configurationAction: (ConfigurationContext) -> Unit,
    override val initializationAction: (InitializationContext) -> Unit,
    override val updateAction: (UpdateContext) -> Unit,
) : ProceduralKernel {

    override fun variables(): Collection<Variable<*>> {
        return variables.values
    }

    override fun variable(name: String): Variable<*>? {
        return variables[name]
    }

    override fun createVariant(base: PartialParameterStateProvider, lambda: KernelVariantBuilder.() -> Unit): Result<KernelVariantImpl> {
        val config = KernelVariantBuilderImpl(this, base).apply(lambda).build()
        return generateVariant(config)
    }

    fun generateVariant(config: KernelVariantData): Result<KernelVariantImpl> {
        // Check that all parameters have been assigned a value.
        for (parameter in parameters()) {
            if (!config.hasValue(parameter)) {
                throw IllegalArgumentException("Incomplete kernel configuration, parameter \"${parameter.name}\" has no value set")
            }
        }

        // Start kernel configuration phase.
        val context = ConfigurationContextImpl(this, config)
        try {
            configurationAction(context)
        } catch (exception: KernelConfigurationException) {
            return Result.failure(exception)
        }

        // Build variant.
        val variant = KernelVariantImpl(
            this,
            context.variables,
            config.parameterStateStorage,
            context.vectorSizeStorage.copy(),
        )
        return Result.success(variant)
    }

    fun generateInstance(config: KernelInstanceConfig): Result<KernelInstanceImpl> {
        val kernelVariant = config.kernelVariant

        // Check that all settings have been assigned a value.
        for (setting in settings()) {
            if (!config.hasValue(setting)) {
                throw IllegalArgumentException("Incomplete kernel configuration, setting \"${setting.name}\" has no value set")
            }
        }

        // Generate initial field states.
        val fieldStatePatch = generateInitialFieldStatePatch(kernelVariant, config)

        // Generate initial output states.
        val outputStatePatch = generateInitialOutputStatePatch(kernelVariant, config)

        // Initialize the kernel instance.
        val context = InitializationContextImpl(this, config.kernelVariant, config.settingStateStorage, fieldStatePatch, outputStatePatch)
        try {
            initializationAction(context)
        } catch (exception: KernelInitializationException) {
            return Result.failure(exception)
        }

        // Check that all fields have been assigned a value.
        for (field in fields()) {
            if (!fieldStatePatch.hasValue(field)) {
                throw IllegalArgumentException("Incomplete kernel initialization, field \"${field.name}\" has not been initialized")
            }
        }

        // Create the kernel instance.
        val instance = KernelInstanceImpl(
            config.kernelVariant,
            config.settingStateStorage,
            fieldStatePatch.createStorage(),
            outputStatePatch.createStorage(),
        )
        return Result.success(instance)
    }

    fun updateKernel(state: MutableKernelState) {
        val context = UpdateContextImpl(this, state)
        updateAction(context)
    }

    override fun createSpecialization(
        additionalTags: Set<Identifier>,
        additionalProperties: Map<Identifier, String>,
        lambda: MutablePartialParameterStateProvider.() -> Unit,
    ): SpecializedKernel {
        val patch = MutableParameterStatePatch(this, emptyMap())
        patch.lambda()
        return SpecializedKernelImpl(this, patch, additionalTags, additionalProperties)
    }

    override fun generateDefaultParameterStates(): ParameterStatePatch {
        val parameterStates = mutableMapOf<String, Any?>()
        for (parameter in parameters()) {
            val initialization = parameter.initialization ?: continue
            parameterStates[parameter.name] = initialization.invoke()
        }
        return ParameterStatePatch(this, parameterStates)
    }
}
