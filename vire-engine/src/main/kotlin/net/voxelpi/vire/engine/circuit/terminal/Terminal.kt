package net.voxelpi.vire.engine.circuit.terminal

import net.voxelpi.vire.engine.circuit.CircuitElement
import net.voxelpi.vire.engine.circuit.CircuitElementImpl
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.circuit.network.NetworkNodeHolder
import net.voxelpi.vire.engine.circuit.network.NetworkNodeImpl
import java.util.UUID

/**
 * Terminal allow circuits to exchange data with the outside.
 */
public interface Terminal : CircuitElement

internal class TerminalImpl(
    override val circuit: CircuitImpl,
    override val uniqueId: UUID = UUID.randomUUID(),
) : CircuitElementImpl(), Terminal, NetworkNodeHolder {

    override val node: NetworkNodeImpl

    init {
        val network = circuit.createNetwork()
        node = circuit.createNetworkNode(network, uniqueId)
        node.holder = this
    }

    override fun remove() {
        TODO("Not yet implemented")
    }
}
