package net.voxelpi.vire.engine.circuit

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.circuit.component.Component
import net.voxelpi.vire.engine.circuit.network.Network
import net.voxelpi.vire.engine.kernel.KernelState
import net.voxelpi.vire.engine.kernel.MutableKernelState
import java.util.UUID

public interface CircuitState {

    public fun clone(): CircuitState

    public fun mutableClone(): MutableCircuitState

    public operator fun get(component: Component): KernelState

    public operator fun get(network: Network): LogicState
}

public interface MutableCircuitState : CircuitState {

    override fun clone(): MutableCircuitState

    override fun get(component: Component): MutableKernelState

    public operator fun set(component: Component, state: KernelState)

    public operator fun set(network: Network, state: LogicState)

    public fun resetNetworkStates()
}

internal class MutableCircuitStateImpl(
    val componentStates: MutableMap<UUID, MutableKernelState>,
    val networkStates: MutableMap<UUID, LogicState>,
) : MutableCircuitState {

    override fun clone(): MutableCircuitStateImpl = mutableClone()

    override fun mutableClone(): MutableCircuitStateImpl {
        return MutableCircuitStateImpl(
            componentStates.mapValues { (_, state) -> state.mutableCopy() }.toMutableMap(),
            networkStates.mapValues { (_, state) -> state.clone() }.toMutableMap(),
        )
    }

    override fun get(network: Network): LogicState {
        return networkStates[network.uniqueId]!!
    }

    override fun set(network: Network, state: LogicState) {
        networkStates[network.uniqueId] = state
    }

    override fun get(component: Component): MutableKernelState {
        return componentStates[component.uniqueId]!!
    }

    override fun set(component: Component, state: KernelState) {
        require(state is MutableKernelState)
        componentStates[component.uniqueId] = state
    }

    override fun resetNetworkStates() {
        TODO("Not yet implemented")
    }
}

internal fun emptyCircuitState(): MutableCircuitStateImpl {
    return MutableCircuitStateImpl(mutableMapOf(), mutableMapOf())
}
