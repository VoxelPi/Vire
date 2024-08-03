package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.procedual.ProceduralKernelBuilder
import net.voxelpi.vire.engine.kernel.procedual.ProceduralKernelBuilderImpl
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.patch.MutableParameterStatePatch
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialParameterStateProvider

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

    public fun createVariantData(
        config: KernelVariantConfig
    ): Result<KernelVariantData>

    public fun createVariant(
        vararg patches: PartialParameterStateProvider,
        lambda: KernelVariantBuilder.() -> Unit = {}
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
            )
        )
    }

    /**
     * Generates a new [ParameterStateProvider] with the default value of each parameter.
     */
    public fun defaultParameterStates(): PartialParameterStateProvider

//    /**
//     * Creates a new variant of the kernel using the given [lambda] to initialize the parameters.
//     * Before the builder is run, all parameters of the kernel are initialized to their default values,
//     * therefore the builder doesn't have to set every parameter.
//     *
//     * @param base A parameter state provider that should be used to initialize all parameters before the lambda is run.
//     * @param lambda the receiver lambda which will be invoked on the builder.
//     */
//    public fun createVariant(
//        base: PartialParameterStateProvider = generateDefaultParameterStates(),
//        lambda: KernelVariantBuilder.() -> Unit = {},
//    ): Result<KernelVariant>
//
//    /**
//     * Creates a new variant of the kernel using the given [values] as the state of the parameters.
//     * The value map doesn't have to contain entries for every parameter,
//     * parameters without specified value are set to their default value.
//     * However, the value map must not have any entries for parameters that do not belong to the kernel.
//     *
//     * @param base A parameter state provider that should be used to initialize all parameters that are not specified in the map.
//     * @param values the values that should be applied to the kernel configuration.
//     */
//    public fun createVariant(
//        values: ParameterStateMap,
//        base: PartialParameterStateProvider = generateDefaultParameterStates(),
//    ): Result<KernelVariant> {
//        return createVariant(base) {
//            for ((key, value) in values) {
//                this[key] = value
//            }
//        }
//    }

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
