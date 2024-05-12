package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateMap

/**
 * Provides a kernel.
 */
public interface KernelProvider {

    /**
     * The provided kernel.
     */
    public val kernel: Kernel

    /**
     * Creates a new variant of the kernel using the default value of each parameter.
     *
     * @param base A parameter state provider that should be used to initialize all parameters.
     */
    public fun createVariant(
        base: ParameterStateProvider = kernel.generateDefaultParameterStates(),
    ): Result<KernelVariant> = kernel.createVariant(base)

    /**
     * Creates a new variant of the kernel using the given [lambda] to initialize the parameters.
     * Before the builder is run, all parameters of the kernel are initialized to their default values,
     * therefore the builder doesn't have to set every parameter.
     *
     * @param base A parameter state provider that should be used to initialize all parameters before the lambda is run.
     * @param lambda the receiver lambda which will be invoked on the builder.
     */
    public fun createVariant(
        base: ParameterStateProvider = kernel.generateDefaultParameterStates(),
        lambda: KernelVariantBuilder.() -> Unit,
    ): Result<KernelVariant> = kernel.createVariant(base, lambda)

    /**
     * Creates a new variant of the kernel using the given [values] as the state of the parameters.
     * The value map doesn't have to contain entries for every parameter,
     * parameters without specified value are set to their default value.
     * However, the value map must not have any entries for parameters that do not belong to the kernel.
     *
     * @param base A parameter state provider that should be used to initialize all parameters that are not specified in the map.
     * @param values the values that should be applied to the kernel configuration.
     */
    public fun createVariant(
        values: ParameterStateMap,
        base: ParameterStateProvider = kernel.generateDefaultParameterStates(),
    ): Result<KernelVariant> = kernel.createVariant(values, base)
}
