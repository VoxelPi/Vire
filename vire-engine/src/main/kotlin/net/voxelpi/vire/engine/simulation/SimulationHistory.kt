package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.engine.kernel.KernelVariantImpl

internal class SimulationHistory(
    val kernelInstance: KernelVariantImpl,
) {
    val states: MutableList<SimulationState> = mutableListOf()

    var currentStep: Int = 0
        private set

    init {
        val state = kernelInstance
    }
}
