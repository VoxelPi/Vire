package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider

/**
 * Provides a kernel.
 */
public interface KernelProvider : Kernel {

    /**
     * The provided kernel.
     */
    public val kernel: Kernel

    override val tags: Set<Identifier>
        get() = kernel.tags

    override val properties: Map<Identifier, String>
        get() = kernel.properties

    override fun createVariantData(parameterStates: ParameterStateProvider): Result<KernelVariantData> {
        return kernel.createVariantData(parameterStates)
    }

    override fun createInstanceData(
        variables: VariableProvider,
        vectorSizes: VectorSizeProvider,
        parameterStates: ParameterStateProvider,
        settingStates: SettingStateProvider,
    ): Result<KernelInstanceData> {
        return kernel.createInstanceData(variables, vectorSizes, parameterStates, settingStates)
    }

    override fun initialKernelState(
        variables: VariableProvider,
        vectorSizes: VectorSizeProvider,
        parameterStates: ParameterStateProvider,
        settingStates: SettingStateProvider,
        fieldStates: FieldStateProvider,
        inputStates: InputStateProvider,
        outputStates: OutputStateProvider,
    ): MutableKernelState {
        return kernel.initialKernelState(variables, vectorSizes, parameterStates, settingStates, fieldStates, inputStates, outputStates)
    }

    override fun updateKernelState(state: MutableKernelState) {
        return kernel.updateKernelState(state)
    }

    override fun variables(): Collection<Variable<*>> {
        return kernel.variables()
    }

    override fun variable(name: String): Variable<*>? {
        return kernel.variable(name)
    }
}
