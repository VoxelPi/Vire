package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialParameterStateProvider

internal interface ParameterStatePatchWrapper : PartialParameterStateProvider {

    val parameterStatePatch: ParameterStatePatch

    override val variableProvider: VariableProvider
        get() = parameterStatePatch.variableProvider

    override fun <T> get(parameter: Parameter<T>): T {
        return parameterStatePatch[parameter]
    }

    override fun hasValue(parameter: Parameter<*>): Boolean {
        return parameterStatePatch.hasValue(parameter)
    }

    override fun allParametersSet(): Boolean {
        return parameterStatePatch.allParametersSet()
    }
}

internal interface MutableParameterStatePatchWrapper : ParameterStatePatchWrapper, MutablePartialParameterStateProvider {

    override val parameterStatePatch: MutableParameterStatePatch

    override fun <T> set(parameter: Parameter<T>, value: T) {
        parameterStatePatch[parameter] = value
    }
}
