package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.engine.circuit.component.ComponentState
import net.voxelpi.vire.engine.circuit.kernel.KernelState
import net.voxelpi.vire.engine.circuit.network.NetworkState
import java.util.UUID

public interface SimulationState

internal class SimulationStateImpl : SimulationState {

    private val kernelState: KernelState = TODO()
    private val componentStates: MutableMap<UUID, ComponentState> = mutableMapOf()
    private val networkStates: MutableMap<UUID, NetworkState> = mutableMapOf()
}
