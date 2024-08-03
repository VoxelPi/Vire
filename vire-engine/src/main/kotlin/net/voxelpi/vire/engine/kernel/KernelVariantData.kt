package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.PartialSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProviderWrapper

public data class KernelVariantData(
    public val kernel: Kernel,
    override val parameterStateProvider: ParameterStateProvider,
    override val variableProvider: VariableProvider,
    override val vectorSizeProvider: VectorSizeProvider,
    public val initialSettingStateProvider: PartialSettingStateProvider,
    public val initialFieldStateProvider: PartialSettingStateProvider,
    public val initialOutputStateProvider: PartialSettingStateProvider,
) : ParameterStateProviderWrapper, VectorSizeProviderWrapper
