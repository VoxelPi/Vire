package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.ParameterStateMap
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider

internal data class KernelVariantConfig(
    override val kernel: KernelImpl,
    override val variableStates: Map<String, Any?>,
) : ParameterStateMap {

    constructor(kernel: KernelImpl, parameterStateProvider: ParameterStateProvider) :
        this(kernel, kernel.parameters().associate { it.name to parameterStateProvider[it] })

    init {
        for (parameterName in variableStates.keys) {
            // Check that only existing parameters are specified.
            require(kernel.hasParameter(parameterName)) { "Specified value for unknown parameter '$parameterName'" }
        }
        for (parameter in kernel.parameters()) {
            // Check that every parameter has an assigned value.
            require(parameter.name in variableStates) { "No value for the parameter ${parameter.name}" }
            // Check that the assigned value is valid for the given parameter.
            require(parameter.isValidTypeAndValue(variableStates[parameter.name])) { "Invalid value for the parameter ${parameter.name}" }
        }
    }
}
