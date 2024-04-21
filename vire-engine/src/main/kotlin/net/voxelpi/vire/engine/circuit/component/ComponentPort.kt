package net.voxelpi.vire.engine.circuit.component

import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.circuit.network.NetworkImpl
import net.voxelpi.vire.engine.circuit.network.NetworkNodeHolder
import net.voxelpi.vire.engine.circuit.network.NetworkNodeImpl
import java.util.UUID

public interface ComponentPort {

    public val circuit: Circuit

    public val component: Component

    public fun remove()
}

internal class ComponentPortImpl(
    override val component: ComponentImpl,
    val uniqueId: UUID = UUID.randomUUID(),
) : ComponentPort, NetworkNodeHolder {

    override val node: NetworkNodeImpl

    override val circuit: CircuitImpl
        get() = component.circuit

    override var network: NetworkImpl
        get() = node.network
        set(value) {
            node.network = value
        }

    init {
        val network = circuit.createNetwork()
        node = circuit.createNetworkNode(network, uniqueId)
        node.holder = this
    }

    override fun remove() {
        component.removePort(this)
    }

    fun destroy() {
        TODO("Not yet implemented")
    }
}
