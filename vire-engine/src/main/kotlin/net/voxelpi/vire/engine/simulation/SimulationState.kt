package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.KernelState
import java.util.UUID

public interface SimulationState

internal class SimulationStateImpl : SimulationState {

    private val kernelState: KernelState = TODO()
    private val componentStates: MutableMap<UUID, KernelState> = mutableMapOf()
    private val networkStates: MutableMap<UUID, LogicState> = mutableMapOf()
}
