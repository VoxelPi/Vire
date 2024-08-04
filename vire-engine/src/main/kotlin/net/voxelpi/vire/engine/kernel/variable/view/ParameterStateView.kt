package net.voxelpi.vire.engine.kernel.variable.view

import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableParameterStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialParameterStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.PartialParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialParameterStateProviderWrapper

internal class PartialParameterStateView(
    override val parameterStateProvider: PartialParameterStateProvider,
    override val variableProvider: VariableProvider,
) : PartialParameterStateProviderWrapper

internal class ParameterStateView(
    override val parameterStateProvider: ParameterStateProvider,
    override val variableProvider: VariableProvider,
) : ParameterStateProviderWrapper

internal class MutablePartialParameterStateView(
    override val parameterStateProvider: MutablePartialParameterStateProvider,
    override val variableProvider: VariableProvider,
) : MutablePartialParameterStateProviderWrapper

internal class MutableParameterStateView(
    override val parameterStateProvider: MutableParameterStateProvider,
    override val variableProvider: VariableProvider,
) : MutableParameterStateProviderWrapper
