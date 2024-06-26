package net.voxelpi.vire.engine.circuit.component

import net.voxelpi.event.post
import net.voxelpi.vire.engine.circuit.CircuitElement
import net.voxelpi.vire.engine.circuit.CircuitElementImpl
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.circuit.event.component.ComponentPortSelectVariableEvent
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
 * A port of a component.
 * Interfaces the specified state machine input or output to connected network.
 */
public interface ComponentPort : CircuitElement {

    /**
     * The component the port belongs to.
     */
    public val component: Component

    /**
     * The variable that should be bound to the port.
     */
    public var variable: InterfaceVariable?

    /**
     * The network the port is part of.
     */
    public val network: Network

    /**
     * The network node of the port.
     */
    public val networkNode: NetworkNode

    /**
     * If the port is assigned to an input of the kernel.
     */
    public val isInput: Boolean

    /**
     * If the port is assigned to an output of the kernel.
     */
    public val isOutput: Boolean
}

internal class ComponentPortImpl(
    override val component: ComponentImpl,
    variable: InterfaceVariable?,
    override val uniqueId: UUID = UUID.randomUUID(),
) : CircuitElementImpl(), ComponentPort, NetworkNodeHolder {

    override var variable: InterfaceVariable? = variable
        set(value) {
            circuit.eventScope.post(ComponentPortSelectVariableEvent(this, value, field))
            field = value
        }

    override val circuit: CircuitImpl
        get() = component.circuit

    override val networkNode: NetworkNodeImpl = circuit.createNetworkNode(uniqueId = uniqueId).apply {
        holder = this@ComponentPortImpl
    }

    override val network: NetworkImpl
        get() = networkNode.network

    override fun remove() {
        component.removePort(this)
    }

    fun destroy() {
        circuit.removeNetworkNode(networkNode)
    }

    override val isInput: Boolean
        get() = variable != null && variable is Input

    override val isOutput: Boolean
        get() = variable != null && variable is Output
}
