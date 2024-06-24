package net.voxelpi.vire.engine.circuit

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.circuit.component.Component
import net.voxelpi.vire.engine.circuit.network.Network
import net.voxelpi.vire.engine.kernel.KernelState
import net.voxelpi.vire.engine.kernel.MutableKernelState
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.OutputVectorElement
import net.voxelpi.vire.engine.kernel.variable.provider.InputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableOutputStateProvider
import java.util.UUID

public interface CircuitState {

    public val circuitInstance: CircuitInstance

    public val circuit: Circuit
        get() = circuitInstance.circuit

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
    override val circuitInstance: MutableCircuitInstanceImpl,
    val componentStates: MutableMap<UUID, MutableKernelState>,
    val networkStates: MutableMap<UUID, LogicState>,
) : MutableCircuitState {

    override val circuit: CircuitImpl
        get() = circuitInstance.circuit

    override fun clone(): MutableCircuitStateImpl = mutableClone()

    override fun mutableClone(): MutableCircuitStateImpl {
        return MutableCircuitStateImpl(
            circuitInstance,
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
        for (networkUniqueId in networkStates.keys) {
            networkStates[networkUniqueId] = LogicState.EMPTY
        }
    }

    fun initialize(circuit: CircuitImpl, circuitInstance: CircuitInstance): Result<Unit> {
        componentStates.clear()
        networkStates.clear()

        // Create the initial component states.
        for (component in circuit.components()) {
            val componentInstance = circuitInstance[component]
            val componentState = componentInstance.initialKernelState()
            componentStates[component.uniqueId] = componentState
        }

        // Create the existing network states.
        for (network in circuit.networks()) {
            networkStates[network.uniqueId] = LogicState.EMPTY
        }

        return Result.success(Unit)
    }

    fun updateCircuitInputs(context: InputStateProvider) {
        // Push all terminal inputs -> network states.
        // This is where the input variables of the circuit kernel are read.
        // This assumes that the network states have been previously cleared.
        for (terminal in circuit.terminals()) {
            this[terminal.network] += when (val variable = terminal.variable ?: continue) {
                is InputScalar -> context[variable]
                is InputVectorElement -> context[variable]
                is OutputScalar, is OutputVectorElement -> continue
            }
        }
    }

    fun updateComponentInputs() {
        // Push all network states -> port inputs.
        for (component in circuit.components()) {
            val componentState = this[component]

            // Clear all input variables.
            for (input in component.kernel.inputs()) {
                when (input) {
                    is InputScalar -> componentState[input] = LogicState.EMPTY
                    is InputVector -> componentState[input] = Array(component.kernelVariant.size(input)) { LogicState.EMPTY }
                    is InputVectorElement -> throw IllegalStateException("Vector elements are not supported.")
                }
            }

            // Pull all input ports.
            for (port in component.ports()) {
                when (val variable = port.variable ?: continue) {
                    is InputScalar -> componentState[variable] += this[port.network]
                    is InputVectorElement -> componentState[variable] += this[port.network]
                    is OutputScalar, is OutputVectorElement -> continue
                }
            }
        }
    }

    fun updateComponents() {
        // Update the kernels of all components.
        for (component in circuit.components()) {
            val componentState = this[component]
            component.kernel.updateKernel(componentState)
        }
    }

    fun updateComponentOutputs() {
        // Reset all network states.
        resetNetworkStates()

        // Push all port outputs -> network states.
        for (component in circuit.components()) {
            val componentState = this[component]
            for (port in component.ports()) {
                this[port.network] += when (val variable = port.variable ?: continue) {
                    is OutputScalar -> componentState[variable]
                    is OutputVectorElement -> componentState[variable]
                    is InputScalar, is InputVectorElement -> continue
                }
            }
        }
    }

    fun updateCircuitOutputs(context: MutableOutputStateProvider) {
        // Clear all output variables.
        for (output in circuit.outputs()) {
            when (output) {
                is OutputScalar -> context[output] = LogicState.EMPTY
                is OutputVector -> context[output] = Array(circuit.size(output)) { LogicState.EMPTY }
                is OutputVectorElement -> throw IllegalStateException("vector elements are not supported")
            }
        }

        // Push all network states -> terminal outputs.
        // This is where the output variables of the circuit kernel are written.
        for (terminal in circuit.terminals()) {
            when (val variable = terminal.variable ?: continue) {
                is OutputScalar -> context[variable] += this[terminal.network]
                is OutputVectorElement -> context[variable] += this[terminal.network]
                is InputScalar, is InputVectorElement -> continue
            }
        }
    }

    companion object {
        fun createEmpty(circuitInstance: MutableCircuitInstanceImpl): MutableCircuitStateImpl {
            return MutableCircuitStateImpl(circuitInstance, mutableMapOf(), mutableMapOf())
        }
    }
}
