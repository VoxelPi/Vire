package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProviderWrapper

public data class KernelVariantConfig(
    public val kernel: Kernel,
    override val parameterStateProvider: ParameterStateProvider,
) : ParameterStateProviderWrapper
