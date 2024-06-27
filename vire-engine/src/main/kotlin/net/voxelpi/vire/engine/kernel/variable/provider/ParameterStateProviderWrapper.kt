package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

internal interface ParameterStateProviderWrapper : ParameterStateProvider {

    val parameterStateProvider: ParameterStateProvider

    override val variableProvider: VariableProvider
        get() = parameterStateProvider.variableProvider

    override fun <T> get(parameter: Parameter<T>): T {
        return parameterStateProvider[parameter]
    }
}

internal interface MutableParameterStateProviderWrapper : ParameterStateProviderWrapper, MutableParameterStateProvider {

    override val parameterStateProvider: MutableParameterStateProvider

    override fun <T> set(parameter: Parameter<T>, value: T) {
        parameterStateProvider[parameter] = value
    }
}
