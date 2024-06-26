package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.ParameterStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.storage.parameterStateStorage

internal data class KernelVariantConfig(
    val kernel: KernelImpl,
    override val parameterStateStorage: ParameterStateStorage,
) : ParameterStateStorageWrapper {

    constructor(kernel: KernelImpl, parameterStateProvider: ParameterStateProvider) :
        this(kernel, parameterStateStorage(kernel, parameterStateProvider))

    constructor(kernel: KernelImpl, parameterStateMap: ParameterStateMap) :
        this(kernel, parameterStateStorage(kernel, parameterStateMap))
}
