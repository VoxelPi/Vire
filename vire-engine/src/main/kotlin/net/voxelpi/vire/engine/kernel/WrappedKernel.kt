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

public interface WrappedKernel : Kernel {

    public val kernel: Kernel

    override val tags: Set<Identifier>
        get() = kernel.tags

    override val properties: Map<Identifier, String>
        get() = kernel.properties

    override fun createVariantData(parameterStates: ParameterStateProvider): Result<KernelVariantData> {
        val data = kernel.createVariantData(parameterStates).getOrElse { return Result.failure(it) }

        return Result.success(
            KernelVariantData(
                this,
                data.variableProvider,
                data.vectorSizeProvider,
                data.parameterStateProvider,
                data.initialSettingStateProvider,
            )
        )
    }

    override fun createInstanceData(
        variables: VariableProvider,
        vectorSizes: VectorSizeProvider,
        parameterStates: ParameterStateProvider,
        settingStates: SettingStateProvider,
    ): Result<KernelInstanceData> {
        val data = kernel.createInstanceData(
            variables,
            vectorSizes,
            parameterStates,
            settingStates,
        ).getOrElse { return Result.failure(it) }

        return Result.success(
            KernelInstanceData(
                this,
                data.variableProvider,
                data.vectorSizeProvider,
                data.parameterStateProvider,
                data.settingStateProvider,
                data.fieldStateProvider,
                data.outputStateProvider,
            )
        )
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
        val state = kernel.initialKernelState(
            variables,
            vectorSizes,
            parameterStates,
            settingStates,
            fieldStates,
            inputStates,
            outputStates,
        ) as MutableKernelStateImpl

        return MutableKernelStateImpl(
            this,
            state.variableProvider,
            state,
            state,
            state,
            state.fieldStateStorage,
            state.inputStateStorage,
            state.outputStateStorage,
        )
    }

    override fun updateKernelState(state: MutableKernelState) {
        kernel.updateKernelState(state)
    }

    override fun variables(): Collection<Variable<*>> {
        return kernel.variables()
    }

    override fun variable(name: String): Variable<*>? {
        return kernel.variable(name)
    }
}
