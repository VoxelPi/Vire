package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.engine.circuit.component.ComponentState
import net.voxelpi.vire.engine.circuit.network.NetworkState
import net.voxelpi.vire.engine.kernel.KernelInstance
import java.util.UUID

public interface SimulationState

internal class SimulationStateImpl : SimulationState {

    private val kernelInstance: KernelInstance = TODO()
    private val componentStates: MutableMap<UUID, ComponentState> = mutableMapOf()
    private val networkStates: MutableMap<UUID, NetworkState> = mutableMapOf()
}
