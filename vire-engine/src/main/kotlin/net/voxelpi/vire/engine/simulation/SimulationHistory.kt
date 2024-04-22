package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.engine.circuit.kernel.KernelInstanceImpl

internal class SimulationHistory(
    val kernelInstance: KernelInstanceImpl,
) {
    val states: MutableList<SimulationState> = mutableListOf()

    var currentStep: Int = 0
        private set

    init {
        val state = kernelInstance
    }
}
