package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProviderWrapper

public sealed interface Output : IOVariable

/**
 * A kernel output scalar, they are used to transfer a single [net.voxelpi.vire.engine.LogicState] from the kernel to a circuit network.
 * Their value can be read and modified in kernel updates as well as in the kernel initialization.
 */
public data class OutputScalar internal constructor(
    override val name: String,
    public val initialization: OutputScalarInitialization,
) : IOScalarVariable, Output

/**
 * The output scalar initialization type.
 */
public typealias OutputScalarInitialization = OutputScalarInitializationContext.() -> LogicState

/**
 * A kernel output vector, they are used to transfer multiple [net.voxelpi.vire.engine.LogicState] from the kernel to a circuit network.
 * Their value can be read and modified in kernel updates as well as in the kernel initialization.
 */
public data class OutputVector internal constructor(
    override val name: String,
    override val size: VectorSizeInitializationContext.() -> Int,
    public val initialization: OutputVectorInitialization,
) : IOVectorVariable, Output {

    override fun get(index: Int): OutputVectorElement {
        return OutputVectorElement(this, index)
    }
}

/**
 * The output scalar initialization type.
 */
public typealias OutputVectorInitialization = OutputVectorInitializationContext.(index: Int) -> LogicState

/**
 * An element of an output vector.
 */
public data class OutputVectorElement internal constructor(
    override val vector: OutputVector,
    override val index: Int,
) : IOVectorVariableElement, Output

/**
 * The initialization context of an output scalar.
 */
public class OutputScalarInitializationContext internal constructor(
    override val variableProvider: VariableProvider,
    override val vectorSizeProvider: VectorSizeProvider,
    override val parameterStateProvider: ParameterStateProvider,
    override val settingStateProvider: SettingStateProvider,
) : VariableProvider, VectorSizeProviderWrapper, ParameterStateProviderWrapper, SettingStateProviderWrapper {

    override fun variables(): Collection<Variable<*>> = variableProvider.variables()

    override fun variable(name: String): Variable<*>? = variableProvider.variable(name)
}

/**
 * The initialization context of an output vector.
 */
public class OutputVectorInitializationContext internal constructor(
    override val variableProvider: VariableProvider,
    override val vectorSizeProvider: VectorSizeProvider,
    override val parameterStateProvider: ParameterStateProvider,
    override val settingStateProvider: SettingStateProvider,
) : VariableProvider, VectorSizeProviderWrapper, ParameterStateProviderWrapper, SettingStateProviderWrapper {

    override fun variables(): Collection<Variable<*>> = variableProvider.variables()

    override fun variable(name: String): Variable<*>? = variableProvider.variable(name)
}

/**
 * A build for an output scalar.
 *
 * @property name The name of the output scalar.
 */
public class OutputScalarBuilder internal constructor(
    public val name: String,
) {

    /**
     * The initialization for the value of the output scalar.
     */
    public var initialization: OutputScalarInitialization = { LogicState.EMPTY }
}

public class OutputVectorBuilder internal constructor(
    public val name: String,
) {

    /**
     * The initialization for the value of the output vector.
     */
    public var initialization: OutputVectorInitialization = { LogicState.EMPTY }

    /**
     * The initial size of the output vector.
     * Note that the size of a vector variable can be set to a different value during the configuration of a kernel.
     */
    public var size: VectorSizeInitializationContext.() -> Int = { 0 }
}

/**
 * Creates a new output scalar with the given [name] using the given [lambda].
 */
public fun createOutput(name: String, lambda: OutputScalarBuilder.() -> Unit = {}): OutputScalar {
    val builder = OutputScalarBuilder(name)
    builder.lambda()
    return OutputScalar(name, builder.initialization)
}

/**
 * Creates a new output vector with the given [name] using the given [lambda].
 */
public fun createOutputVector(name: String, lambda: OutputVectorBuilder.() -> Unit = {}): OutputVector {
    val builder = OutputVectorBuilder(name)
    builder.lambda()
    return OutputVector(name, builder.size, builder.initialization)
}
