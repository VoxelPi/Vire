package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.MutableVariableRegistry
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.VariableProviderView
import net.voxelpi.vire.engine.kernel.variable.patch.MutableParameterStatePatch
import net.voxelpi.vire.engine.kernel.variable.patch.ParameterStatePatch
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider

public interface SpecializedKernel : WrappedKernel {

    public val specifiedParameters: PartialParameterStateProvider
}

internal open class SpecializedKernelImpl(
    final override val kernel: Kernel,
    specifiedParameters: PartialParameterStateProvider,
    additionalTags: Set<Identifier>,
    additionalProperties: Map<Identifier, String>,
) : SpecializedKernel {

    override val specifiedParameters: ParameterStatePatch = ParameterStatePatch(kernel, specifiedParameters)

    override val tags: Set<Identifier> = kernel.tags + additionalTags

    override val properties: Map<Identifier, String> = kernel.properties + additionalProperties

    override fun createVariantData(parameterStates: ParameterStateProvider): Result<KernelVariantData> {
        // Build config for parent kernel.
        val baseParameters = MutableParameterStatePatch(kernel).let {
            it.applyParameterStatePatch(parameterStates)
            it.applyParameterStatePatch(specifiedParameters)
            it.createStorage()
        }

        // Generate data using parent kernel.
        val baseData = kernel.createVariantData(baseParameters).getOrElse { return Result.failure(it) }

        // Filter variables.
        val variableProvider = VariableProviderView(baseData.variableProvider) { it !is Parameter<*> || !specifiedParameters.hasValue(it) }

        // Return result.
        return Result.success(
            KernelVariantData(
                this,
                variableProvider,
                baseData.vectorSizeProvider,
                parameterStates,
                baseData.initialSettingStateProvider,
            )
        )
    }

    override fun createInstanceData(
        variables: VariableProvider,
        vectorSizes: VectorSizeProvider,
        parameterStates: ParameterStateProvider,
        settingStates: SettingStateProvider,
    ): Result<KernelInstanceData> {
        // Build config for parent kernel.
        val baseParameterStates = MutableParameterStatePatch(kernel).let {
            it.applyParameterStatePatch(parameterStates)
            it.applyParameterStatePatch(specifiedParameters)
            it.createStorage()
        }

        // Generate data using parent kernel.
        val baseData = kernel.createInstanceData(
            (variables as VariableProviderView).variableProvider,
            vectorSizes,
            baseParameterStates,
            settingStates
        ).getOrElse { return Result.failure(it) }

        // Return result.
        return Result.success(
            KernelInstanceData(
                this,
                variables,
                baseData.vectorSizeProvider,
                parameterStates,
                baseData.settingStateProvider,
                baseData.fieldStateProvider,
                baseData.outputStateProvider,
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
        val baseVariables = MutableVariableRegistry(variables.variables())
        for (parameter in kernel.parameters()) {
            if (specifiedParameters.hasValue(parameter)) {
                baseVariables.registerVariable(parameter)
            }
        }

        // Build config for parent kernel.
        val baseParameterStates = MutableParameterStatePatch(baseVariables).let {
            it.applyParameterStatePatch(parameterStates)
            it.applyParameterStatePatch(specifiedParameters)
            it.createStorage()
        }

        val baseState = kernel.initialKernelState(
            baseVariables,
            vectorSizes,
            baseParameterStates,
            settingStates,
            fieldStates,
            inputStates,
            outputStates,
        ) as MutableKernelStateImpl

        return MutableKernelStateImpl(
            this,
            variables,
            baseState,
            baseState,
            baseState,
            baseState.fieldStateStorage,
            baseState.inputStateStorage,
            baseState.outputStateStorage,
        )
    }

    override fun updateKernelState(state: MutableKernelState) {
        require(state is MutableKernelStateImpl)

        val baseVariables = MutableVariableRegistry(state.variableProvider.variables())
        for (parameter in kernel.parameters()) {
            if (specifiedParameters.hasValue(parameter)) {
                baseVariables.registerVariable(parameter)
            }
        }

        val baseState = MutableKernelStateImpl(
            this,
            baseVariables,
            state,
            state,
            state,
            state.fieldStateStorage,
            state.inputStateStorage,
            state.outputStateStorage,
        )
        kernel.updateKernelState(baseState)
    }

    override fun variables(): Collection<Variable<*>> {
        return kernel.variables().filter {
            if (it is Parameter<*>) {
                !specifiedParameters.hasValue(it)
            } else {
                true
            }
        }
    }

    override fun variable(name: String): Variable<*>? {
        val variable = kernel.variable(name)
        if (variable is Parameter<*> && specifiedParameters.hasValue(variable)) {
            return null
        }
        return kernel.variable(name)
    }
}
