package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.MutableInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.VectorVariableSizeProvider

public interface SimulationInputConfiguration :
    ParameterStateProvider,
    SettingStateProvider,
    MutableInputStateProvider,
    VectorVariableSizeProvider {

    /**
     * The kernel which of which the instance was created.
     */
    public val kernelVariant: KernelVariant
}

internal class SimulationInputConfigurationImpl(
    override val kernelVariant: KernelVariant,
) : SimulationInputConfiguration {

    override fun <T> get(parameter: Parameter<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T> get(setting: Setting<T>): T {
        TODO("Not yet implemented")
    }

    override fun get(input: InputScalar): LogicState {
        TODO("Not yet implemented")
    }

    override fun get(inputVector: InputVector): Array<LogicState> {
        TODO("Not yet implemented")
    }

    override fun get(inputVector: InputVector, index: Int): LogicState {
        TODO("Not yet implemented")
    }

    override fun set(input: InputScalar, value: LogicState) {
        TODO("Not yet implemented")
    }

    override fun set(inputVector: InputVector, value: LogicState): Array<LogicState> {
        TODO("Not yet implemented")
    }

    override fun set(inputVector: InputVector, index: Int, value: LogicState): LogicState {
        TODO("Not yet implemented")
    }

    override fun size(vector: VectorVariable<*>): Int {
        TODO("Not yet implemented")
    }

    override fun size(vectorName: String): Int {
        TODO("Not yet implemented")
    }
}
