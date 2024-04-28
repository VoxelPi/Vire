package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.VectorVariable
import net.voxelpi.vire.engine.kernel.variable.VectorVariableSizeProvider

public interface KernelInstance : ParameterStateProvider, SettingStateProvider, VectorVariableSizeProvider {

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

    override fun size(vector: VectorVariable<*>): Int {
        TODO("Not yet implemented")
    }

    override fun size(vectorName: String): Int {
        TODO("Not yet implemented")
    }
}
