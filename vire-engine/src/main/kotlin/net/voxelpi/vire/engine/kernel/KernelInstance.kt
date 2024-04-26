package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.IOVectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.Input
import net.voxelpi.vire.engine.kernel.variable.Output
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider

public interface KernelInstance : ParameterStateProvider, SettingStateProvider, IOVectorSizeProvider {

    /**
     * The kernel configuration from which this state was generated.
     */
    public val kernelVariant: KernelVariant
}

internal class KernelInstanceImpl(
    override val kernelVariant: KernelVariant,
) : KernelInstance {

    override fun <T> get(parameter: Parameter<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T> get(setting: Setting<T>): T {
        TODO("Not yet implemented")
    }

    override fun size(input: Input): Int {
        TODO("Not yet implemented")
    }

    override fun size(output: Output): Int {
        TODO("Not yet implemented")
    }
}
