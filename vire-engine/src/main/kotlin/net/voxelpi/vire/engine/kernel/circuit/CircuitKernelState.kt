package net.voxelpi.vire.engine.kernel.circuit

import net.voxelpi.vire.engine.circuit.network.NetworkState
import java.util.UUID

internal interface CircuitKernelState

internal class CircuitKernelStateImpl : CircuitKernelState {

    val networkStates: MutableMap<UUID, NetworkState> = mutableMapOf()
}
