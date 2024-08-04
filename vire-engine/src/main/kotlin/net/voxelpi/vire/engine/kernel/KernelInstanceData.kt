package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProviderWrapper

public data class KernelInstanceData(
    val kernel: Kernel,
    override val variableProvider: VariableProvider,
    override val vectorSizeProvider: VectorSizeProvider,
    override val parameterStateProvider: ParameterStateProvider,
    override val settingStateProvider: SettingStateProvider,
    override val fieldStateProvider: FieldStateProvider,
    override val outputStateProvider: OutputStateProvider,
) : VectorSizeProviderWrapper,
    ParameterStateProviderWrapper,
    SettingStateProviderWrapper,
    FieldStateProviderWrapper,
    OutputStateProviderWrapper
