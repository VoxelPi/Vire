package net.voxelpi.vire.engine.circuit.component

import net.voxelpi.event.post
import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.circuit.component.ComponentPort
import net.voxelpi.vire.api.circuit.component.ComponentPortVectorVariable
import net.voxelpi.vire.api.circuit.event.component.port.ComponentPortVariableSelectEvent
import net.voxelpi.vire.api.circuit.statemachine.StateMachineOutput
import net.voxelpi.vire.engine.circuit.VireCircuit
import net.voxelpi.vire.engine.circuit.VireCircuitElement
import net.voxelpi.vire.engine.circuit.network.VireNetwork
import net.voxelpi.vire.engine.circuit.network.VireNetworkNode
import net.voxelpi.vire.engine.circuit.network.VireNetworkNodeHolder
import java.util.UUID

class VireComponentPort(
    override val component: VireComponent,
    variable: ComponentPortVectorVariable?,
    override val uniqueId: UUID = UUID.randomUUID(),
) : VireCircuitElement(), ComponentPort, VireNetworkNodeHolder {

    override var variable: ComponentPortVectorVariable? = variable
        set(value) {
            circuit.eventScope.post(ComponentPortVariableSelectEvent(this, value, field))
            field = value
        }

    override val circuit: VireCircuit
        get() = component.circuit

    override val node: VireNetworkNode

    override var network: VireNetwork
        get() = node.network
        set(value) {
            node.network = value
        }

    init {
        val network = circuit.createNetwork()
        node = circuit.createNetworkNode(network, uniqueId)
        node.holder = this
    }

    override fun pushOutput(): LogicState? {
        val variable = variable ?: return null

        val (vector, index) = variable
        if (vector !is StateMachineOutput) {
            return null
        }

        val output = component.stateMachineInstance.pushOutput(vector, index, network.state)
        network.state = output
        return output
    }

    override fun remove() {
        component.removePort(this)
    }

    fun destroy() {
        circuit.removeNetworkNode(node)
    }
}
