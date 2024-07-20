package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateStorageWrapper

internal data class KernelVariantConfig(
    val kernel: KernelImpl,
    override val parameterStateStorage: ParameterStateStorage,
) : ParameterStateStorageWrapper {

    constructor(kernel: KernelImpl, parameterStateProvider: ParameterStateProvider) :
        this(kernel, ParameterStateStorage(kernel, parameterStateProvider))

    constructor(kernel: KernelImpl, parameterStateMap: ParameterStateMap) :
        this(kernel, ParameterStateStorage(kernel, parameterStateMap))
}
