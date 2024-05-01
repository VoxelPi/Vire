package net.voxelpi.vire.engine.simulation.event

import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.variable.MutableSettingStateMap
import net.voxelpi.vire.engine.kernel.variable.MutableSettingStateProvider

public interface SimulationConfigurationBuilder : MutableSettingStateProvider {

    public val kernelVariant: KernelVariant

    public val kernel: Kernel
        get() = kernelVariant.kernel
}

internal class SimulationConfigurationBuilderImpl(
    override val kernelVariant: KernelVariant,
) : SimulationConfigurationBuilder, MutableSettingStateMap {

    override val variableStates: MutableMap<String, Any?> = mutableMapOf()

    override val kernel: Kernel
        get() = super.kernel
}
