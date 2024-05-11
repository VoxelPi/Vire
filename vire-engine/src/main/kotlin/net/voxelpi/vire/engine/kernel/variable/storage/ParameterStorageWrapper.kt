package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider

internal interface ParameterStateStorageWrapper : ParameterStateProvider {

    val parameterStateStorage: ParameterStateStorage

    override val variableProvider: VariableProvider
        get() = parameterStateStorage.variableProvider

    override fun <T> get(parameter: Parameter<T>): T {
        return parameterStateStorage[parameter]
    }
}

internal interface MutableParameterStateStorageWrapper : ParameterStateStorageWrapper, MutableParameterStateProvider {

    override val parameterStateStorage: MutableParameterStateStorage

    override fun <T> set(parameter: Parameter<T>, value: T) {
        parameterStateStorage[parameter] = value
    }
}
