package net.voxelpi.vire.engine.circuit.terminal

import net.voxelpi.event.post
import net.voxelpi.vire.engine.circuit.CircuitElement
import net.voxelpi.vire.engine.circuit.CircuitElementImpl
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.circuit.event.terminal.TerminalSelectVariableEvent
import net.voxelpi.vire.engine.circuit.network.Network
import net.voxelpi.vire.engine.circuit.network.NetworkImpl
import net.voxelpi.vire.engine.circuit.network.NetworkNode
import net.voxelpi.vire.engine.circuit.network.NetworkNodeHolder
import net.voxelpi.vire.engine.circuit.network.NetworkNodeImpl
import net.voxelpi.vire.engine.kernel.variable.Input
import net.voxelpi.vire.engine.kernel.variable.InterfaceVariable
import net.voxelpi.vire.engine.kernel.variable.Output
import java.util.UUID

/**
 * Terminal allow circuits to exchange data with the outside.
 */
public interface Terminal : CircuitElement {

    /**
     * The variable that should be bound to the terminal.
     */
    public var variable: InterfaceVariable?

    /**
     * The network the terminal is part of.
     */
    public val network: Network

    /**
     * The network node of the terminal.
     */
    public val networkNode: NetworkNode

    /**
     * If the terminal is assigned to an input of the circuit.
     */
    public val isInput: Boolean

    /**
     * If the terminal is assigned to an output of the circuit.
     */
    public val isOutput: Boolean
}

internal class TerminalImpl(
    override val circuit: CircuitImpl,
    variable: InterfaceVariable?,
    override val uniqueId: UUID = UUID.randomUUID(),
) : CircuitElementImpl(), Terminal, NetworkNodeHolder {

    override var variable: InterfaceVariable? = variable
        set(value) {
            circuit.eventScope.post(TerminalSelectVariableEvent(this, value, field))
            field = value
        }

    override val networkNode: NetworkNodeImpl = circuit.createNetworkNode(uniqueId = uniqueId).apply {
        holder = this@TerminalImpl
    }

    override val network: NetworkImpl
        get() = networkNode.network

    override fun remove() {
        circuit.removeTerminal(this)
    }

    fun destroy() {
        circuit.removeNetworkNode(networkNode)
    }

    override val isInput: Boolean
        get() = variable != null && variable is Input

    override val isOutput: Boolean
        get() = variable != null && variable is Output
}
