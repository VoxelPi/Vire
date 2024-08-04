package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.procedual.ProceduralKernelBuilder
import net.voxelpi.vire.engine.kernel.procedual.ProceduralKernelBuilderImpl
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.patch.MutableParameterStatePatch
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider

/**
 * A kernel is logical processor that produces logical outputs from its inputs and other parameters.
 */
public interface Kernel : VariableProvider {

    /**
     * The tags of the kernel.
     */
    public val tags: Set<Identifier>

    /**
     * The properties of the kernel.
     */
    public val properties: Map<Identifier, String>

    public fun createVariantData(parameterStates: ParameterStateProvider): Result<KernelVariantData>

    public fun createInstanceData(
        variables: VariableProvider,
        vectorSizes: VectorSizeProvider,
        parameterStates: ParameterStateProvider,
        settingStates: SettingStateProvider,
    ): Result<KernelInstanceData>

    public fun initialKernelState(
        variables: VariableProvider,
        vectorSizes: VectorSizeProvider,
        parameterStates: ParameterStateProvider,
        settingStates: SettingStateProvider,
        fieldStates: FieldStateProvider,
        inputStates: InputStateProvider,
        outputStates: OutputStateProvider,
    ): MutableKernelState

    public fun updateKernelState(state: MutableKernelState)

    public fun createVariant(
        vararg patches: PartialParameterStateProvider,
        lambda: KernelVariantBuilder.() -> Unit = {},
    ): Result<KernelVariant> {
        // Build initial parameter state patch.
        val parameterStates = MutableParameterStatePatch(this, defaultParameterStates())
        patches.forEach(parameterStates::applyParameterStatePatch)

        // Create builder and apply lambda to create the variant config.
        val builder = KernelVariantBuilderImpl(this, parameterStates)
        builder.lambda()
        val config = builder.build()

        // Generate variant data.
        val data = createVariantData(config).getOrElse { return Result.failure(it) }

        // Create the kernel.
        return Result.success(
            KernelVariantImpl(
                this,
                data.parameterStateProvider,
                data.variableProvider,
                data.vectorSizeProvider,
                data.initialSettingStateProvider,
            )
        )
    }

    /**
     * Creates a new kernel based on this kernel.
     * The specialisation may assign fixed values to some values.
     * Additional tags and properties can also be defined.
     */
    public fun createSpecialization(
        additionalTags: Set<Identifier> = emptySet(),
        additionalProperties: Map<Identifier, String> = emptyMap(),
        lambda: MutablePartialParameterStateProvider.() -> Unit,
    ): SpecializedKernel {
        val patch = MutableParameterStatePatch(this, emptyMap())
        patch.lambda()
        return SpecializedKernelImpl(this, patch, additionalTags, additionalProperties)
    }

    /**
     * Creates a new kernel based on this kernel.
     * The specialisation may assign fixed values to some values.
     * Additional tags and properties can also be defined.
     */
    public fun createSpecialization(
        statePatch: PartialParameterStateProvider,
        additionalTags: Set<Identifier> = emptySet(),
        additionalProperties: Map<Identifier, String> = emptyMap(),
    ): SpecializedKernel {
        return SpecializedKernelImpl(this, statePatch, additionalTags, additionalProperties)
    }
}

/**
 * Creates a new [Kernel] using the given [lambda].
 */
public fun kernel(lambda: ProceduralKernelBuilder.() -> Unit): Kernel {
    val builder = ProceduralKernelBuilderImpl()
    builder.lambda()
    return builder.build()
}
