package net.voxelpi.vire.engine.kernel.procedual

import net.voxelpi.vire.engine.kernel.KernelInitializationException
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialFieldStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialOutputStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProviderWrapper

public interface InitializationContext :
    ParameterStateProvider,
    VectorSizeProvider,
    SettingStateProvider,
    MutablePartialFieldStateProvider,
    MutablePartialOutputStateProvider {

    /**
     * The kernel which of which the instance was created.
     */
    public val kernel: ProceduralKernel

    /**
     * Stops the initialization of the kernel instance.
     * This is intended to allow the kernel to signal an invalid setting state.
     */
    public fun signalInvalidConfiguration(message: String = "Invalid kernel initialization"): Nothing {
        throw KernelInitializationException(message)
    }
}

internal class InitializationContextImpl(
    override val kernel: ProceduralKernel,
    override val variableProvider: VariableProvider,
    override val vectorSizeProvider: VectorSizeProvider,
    override val parameterStateProvider: ParameterStateProvider,
    override val settingStateProvider: SettingStateProvider,
    override val fieldStateProvider: MutablePartialFieldStateProvider,
    override val outputStateProvider: MutablePartialOutputStateProvider,
) : InitializationContext,
    VectorSizeProviderWrapper,
    ParameterStateProviderWrapper,
    SettingStateProviderWrapper,
    MutablePartialFieldStateProviderWrapper,
    MutablePartialOutputStateProviderWrapper
