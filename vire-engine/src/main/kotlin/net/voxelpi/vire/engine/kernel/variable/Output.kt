package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.KernelVariantWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider

public sealed interface Output : IOVariable

public data class OutputScalar internal constructor(
    override val name: String,
    public val initialization: ScalarOutputInitializationContext.() -> LogicState,
) : IOScalarVariable, Output

public data class OutputVector internal constructor(
    override val name: String,
    override val size: VectorVariableSize,
    public val initialization: VectorOutputElementInitializationContext.() -> LogicState,
) : IOVectorVariable, Output {

    override fun get(index: Int): IOVectorVariableElement {
        return OutputVectorElement(this, index)
    }
}

public data class OutputVectorElement internal constructor(
    override val vector: OutputVector,
    override val index: Int,
) : IOVectorVariableElement, Output

public interface ScalarOutputInitializationContext : VariableProvider, ParameterStateProvider, SettingStateProvider, VectorSizeProvider {
    public val kernelVariant: KernelVariant
}

internal class ScalarOutputInitializationContextImpl(
    override val kernelVariant: KernelVariant,
    override val settingStateProvider: SettingStateProvider,
) : ScalarOutputInitializationContext, KernelVariantWrapper, SettingStateProviderWrapper {

    override fun variables(): Collection<Variable<*>> = kernelVariant.variables()

    override fun variable(name: String): Variable<*>? = kernelVariant.variable(name)

    override val variableProvider: VariableProvider
        get() = kernelVariant
}

public interface VectorOutputElementInitializationContext :
    VariableProvider,
    ParameterStateProvider,
    SettingStateProvider,
    VectorSizeProvider {

    public val index: Int

    public val kernelVariant: KernelVariant
}

internal class VectorOutputElementInitializationContextImpl(
    override val kernelVariant: KernelVariant,
    override val settingStateProvider: SettingStateProvider,
    override val index: Int,
) : VectorOutputElementInitializationContext, KernelVariantWrapper, SettingStateProviderWrapper {

    override fun variables(): Collection<Variable<*>> = kernelVariant.variables()

    override fun variable(name: String): Variable<*>? = kernelVariant.variable(name)

    override val variableProvider: VariableProvider
        get() = kernelVariant
}

public interface VectorOutputInitializationContext : VariableProvider, ParameterStateProvider, SettingStateProvider, VectorSizeProvider {
    public val kernelVariant: KernelVariant
}

internal class VectorOutputInitializationContextImpl(
    override val kernelVariant: KernelVariant,
    override val settingStateProvider: SettingStateProvider,
) : VectorOutputInitializationContext, KernelVariantWrapper, SettingStateProviderWrapper {

    override fun variables(): Collection<Variable<*>> = kernelVariant.variables()

    override fun variable(name: String): Variable<*>? = kernelVariant.variable(name)

    override val variableProvider: VariableProvider
        get() = kernelVariant

    operator fun get(index: Int): VectorOutputElementInitializationContextImpl {
        return VectorOutputElementInitializationContextImpl(kernelVariant, settingStateProvider, index)
    }
}

/**
 * Creates a new scalar output variable with the given [name] and [initialization].
 */
public fun output(
    name: String,
    initialization: ScalarOutputInitializationContext.() -> LogicState = { LogicState.EMPTY },
): OutputScalar {
    return OutputScalar(name, initialization)
}

/**
 * Creates a new scalar output variable with the given [name] and [initialization].
 */
public fun output(
    name: String,
    initialization: LogicState,
): OutputScalar {
    return OutputScalar(name, initialization = { initialization })
}

/**
 * Creates a new vector output variable with the given [name] and [size].
 */
public fun output(
    name: String,
    size: VectorVariableSize,
    initialization: VectorOutputElementInitializationContext.() -> LogicState = { LogicState.EMPTY },
): OutputVector {
    return OutputVector(name, size, initialization)
}

/**
 * Creates a new vector output variable with the given [name] and [size].
 */
public fun output(
    name: String,
    size: VectorVariableSize,
    initialization: LogicState,
): OutputVector {
    return OutputVector(name, size, initialization = { initialization })
}

/**
 * Creates a new vector output variable with the given [name] and default [size].
 */
public fun output(
    name: String,
    size: Int,
    initialization: VectorOutputElementInitializationContext.() -> LogicState = { LogicState.EMPTY },
): OutputVector = output(name, VectorVariableSize.Value(size), initialization)

/**
 * Creates a new vector output variable with the given [name] and default [size].
 */
public fun output(
    name: String,
    size: Int,
    initialization: LogicState,
): OutputVector = output(name, VectorVariableSize.Value(size), initialization)

/**
 * Creates a new vector output variable with the given [name] using the given [parameter] as default size.
 */
public fun output(
    name: String,
    parameter: Parameter<Int>,
    initialization: VectorOutputElementInitializationContext.() -> LogicState = { LogicState.EMPTY },
): OutputVector = output(name, VectorVariableSize.Parameter(parameter), initialization)

/**
 * Creates a new vector output variable with the given [name] using the given [parameter] as default size.
 */
public fun output(
    name: String,
    parameter: Parameter<Int>,
    initialization: LogicState,
): OutputVector = output(name, VectorVariableSize.Parameter(parameter), initialization)
