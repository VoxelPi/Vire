package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.variable.Input
import net.voxelpi.vire.engine.kernel.variable.InputSizeProvider
import net.voxelpi.vire.engine.kernel.variable.MutableInputStateProvider
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider

public interface SimulationInputConfiguration : ParameterStateProvider, SettingStateProvider, MutableInputStateProvider, InputSizeProvider {

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

    override fun get(input: Input): Array<LogicState> {
        TODO("Not yet implemented")
    }

    override fun set(input: Input, value: Array<LogicState>) {
        TODO("Not yet implemented")
    }

    override fun size(input: Input): Int {
        TODO("Not yet implemented")
    }
}
