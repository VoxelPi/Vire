package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.ParameterStateMap
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.ParameterStateStorage
import net.voxelpi.vire.engine.kernel.variable.ParameterStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.parameterStateStorage

internal data class KernelVariantConfig(
    val kernel: KernelImpl,
    override val parameterStateStorage: ParameterStateStorage,
) : ParameterStateStorageWrapper {

    constructor(kernel: KernelImpl, parameterStateProvider: ParameterStateProvider) :
        this(kernel, parameterStateStorage(kernel, parameterStateProvider))

    constructor(kernel: KernelImpl, parameterStateMap: ParameterStateMap) :
        this(kernel, parameterStateStorage(kernel, parameterStateMap))
}
