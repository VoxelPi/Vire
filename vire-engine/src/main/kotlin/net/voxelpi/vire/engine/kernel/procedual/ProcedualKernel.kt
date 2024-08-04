package net.voxelpi.vire.engine.kernel.procedual

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelConfigurationException
import net.voxelpi.vire.engine.kernel.KernelInitializationException
import net.voxelpi.vire.engine.kernel.KernelInstanceData
import net.voxelpi.vire.engine.kernel.KernelVariantData
import net.voxelpi.vire.engine.kernel.MutableKernelState
import net.voxelpi.vire.engine.kernel.MutableKernelStateImpl
import net.voxelpi.vire.engine.kernel.variable.Variable
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.patch.MutableFieldStatePatch
import net.voxelpi.vire.engine.kernel.variable.patch.MutableOutputStatePatch
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.storage.MutableFieldStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableInputStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableOutputStateStorage

public interface ProceduralKernel : Kernel {

    public val configurationAction: (ConfigurationContext) -> Unit

    public val initializationAction: (InitializationContext) -> Unit

    public val updateAction: (UpdateContext) -> Unit
}

internal class ProceduralKernelImpl(
    override val tags: Set<Identifier>,
    override val properties: Map<Identifier, String>,
    val variables: Map<String, Variable<*>>,
    override val configurationAction: (ConfigurationContext) -> Unit,
    override val initializationAction: (InitializationContext) -> Unit,
    override val updateAction: (UpdateContext) -> Unit,
) : ProceduralKernel {

    override fun variables(): Collection<Variable<*>> {
        return variables.values
    }

    override fun variable(name: String): Variable<*>? {
        return variables[name]
    }

    override fun createVariantData(
        parameterStates: ParameterStateProvider,
    ): Result<KernelVariantData> {
        // Start kernel configuration phase.
        val context = ConfigurationContextImpl(this, parameterStates)
        try {
            configurationAction(context)
        } catch (exception: KernelConfigurationException) {
            return Result.failure(exception)
        }

        // Build variant.
        val variantData = KernelVariantData(
            this,
            context.variableStorage,
            context.vectorSizeStorage,
            parameterStates,
            context.settingStateProvider,
        )
        return Result.success(variantData)
    }

    override fun createInstanceData(
        variables: VariableProvider,
        vectorSizes: VectorSizeProvider,
        parameterStates: ParameterStateProvider,
        settingStates: SettingStateProvider,
    ): Result<KernelInstanceData> {
        val fieldStatePatch = MutableFieldStatePatch(
            variables,
            variables.defaultFieldStates(vectorSizes, parameterStates, settingStates),
        )
        val outputStatePatch = MutableOutputStatePatch(
            variables,
            variables.defaultOutputStates(vectorSizes, parameterStates, settingStates),
        )

        // Initialize the kernel instance.
        val context = InitializationContextImpl(
            this,
            variables,
            vectorSizes,
            parameterStates,
            settingStates,
            fieldStatePatch,
            outputStatePatch,
        )
        try {
            initializationAction(context)
        } catch (exception: KernelInitializationException) {
            return Result.failure(exception)
        }
        val fieldStates = fieldStatePatch.createStorage()
        val outputStates = outputStatePatch.createStorage()

        return Result.success(
            KernelInstanceData(
                this,
                variables,
                vectorSizes,
                parameterStates,
                settingStates,
                fieldStates,
                outputStates,
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
        return MutableKernelStateImpl(
            this,
            variables,
            vectorSizes,
            parameterStates,
            settingStates,
            MutableFieldStateStorage(variables, fieldStates),
            MutableInputStateStorage(variables, inputStates),
            MutableOutputStateStorage(variables, outputStates),
        )
    }

    override fun updateKernelState(state: MutableKernelState) {
        val context = UpdateContextImpl(this, state)
        updateAction(context)
    }
}
