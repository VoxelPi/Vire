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
    public val initialization: OutputScalarInitializationContext.() -> LogicState,
) : IOScalarVariable, Output

public data class OutputVector internal constructor(
    override val name: String,
    override val size: VectorSizeInitializationContext.() -> Int,
    public val initialization: OutputVectorInitializationContext.(index: Int) -> LogicState,
) : IOVectorVariable, Output {

    override fun get(index: Int): OutputVectorElement {
        return OutputVectorElement(this, index)
    }
}

public data class OutputVectorElement internal constructor(
    override val vector: OutputVector,
    override val index: Int,
) : IOVectorVariableElement, Output

public class OutputScalarInitializationContext internal constructor(
    override val kernelVariant: KernelVariant,
    override val settingStateProvider: SettingStateProvider,
) : VariableProvider, ParameterStateProvider, SettingStateProviderWrapper, VectorSizeProvider, KernelVariantWrapper {

    override fun variables(): Collection<Variable<*>> = kernelVariant.variables()

    override fun variable(name: String): Variable<*>? = kernelVariant.variable(name)

    override val variableProvider: VariableProvider
        get() = kernelVariant
}

public class OutputVectorInitializationContext internal constructor(
    override val kernelVariant: KernelVariant,
    override val settingStateProvider: SettingStateProvider,
) : VariableProvider, ParameterStateProvider, SettingStateProviderWrapper, VectorSizeProvider, KernelVariantWrapper {

    override fun variables(): Collection<Variable<*>> = kernelVariant.variables()

    override fun variable(name: String): Variable<*>? = kernelVariant.variable(name)

    override val variableProvider: VariableProvider
        get() = kernelVariant
}

public class OutputScalarBuilder internal constructor(
    public val name: String,
) {

    public var initialization: OutputScalarInitializationContext.() -> LogicState = { LogicState.EMPTY }
}

public class OutputVectorBuilder internal constructor(
    public val name: String,
) {

    public var initialization: OutputVectorInitializationContext.(index: Int) -> LogicState = { LogicState.EMPTY }

    public var size: VectorSizeInitializationContext.() -> Int = { 0 }
}

public fun createOutput(name: String, lambda: OutputScalarBuilder.() -> Unit = {}): OutputScalar {
    val builder = OutputScalarBuilder(name)
    builder.lambda()
    return OutputScalar(name, builder.initialization)
}

public fun createOutputVector(name: String, lambda: OutputVectorBuilder.() -> Unit = {}): OutputVector {
    val builder = OutputVectorBuilder(name)
    builder.lambda()
    return OutputVector(name, builder.size, builder.initialization)
}
