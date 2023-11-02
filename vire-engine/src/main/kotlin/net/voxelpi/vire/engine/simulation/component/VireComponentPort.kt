package net.voxelpi.vire.engine.simulation.component

import net.voxelpi.vire.api.simulation.component.ComponentPort
import net.voxelpi.vire.api.simulation.component.ComponentPortVectorVariable
import net.voxelpi.vire.api.simulation.component.StateMachineOutput
import net.voxelpi.vire.api.simulation.event.simulation.component.port.ComponentPortVariableSelectEvent
import net.voxelpi.vire.api.simulation.network.NetworkState
import net.voxelpi.vire.engine.simulation.VireSimulation
import net.voxelpi.vire.engine.simulation.VireSimulationObject
import net.voxelpi.vire.engine.simulation.network.VireNetwork
import net.voxelpi.vire.engine.simulation.network.VireNetworkNode
import net.voxelpi.vire.engine.simulation.network.VireNetworkNodeHolder
import java.util.UUID

class VireComponentPort(
    override val component: VireComponent,
    variable: ComponentPortVectorVariable?,
    override val uniqueId: UUID = UUID.randomUUID(),
) : VireSimulationObject(), ComponentPort, VireNetworkNodeHolder {

    override var variable: ComponentPortVectorVariable? = variable
        set(value) {
            simulation.publish(ComponentPortVariableSelectEvent(this, value, field))
            field = value
        }

    override val simulation: VireSimulation
        get() = component.simulation

    override val node: VireNetworkNode

    override var network: VireNetwork
        get() = node.network
        set(value) {
            node.network = value
        }

    init {
        val network = simulation.createNetwork()
        node = simulation.createNetworkNode(network, uniqueId)
        node.holder = this
    }

    override fun pushOutput(): NetworkState? {
        val variable = variable ?: return null

        val (vector, index) = variable
        if (vector !is StateMachineOutput) {
            return null
        }

        val output = component.stateMachineContext.pushOutput(vector, index, network.state)
        network.state = output
        return output
    }

    override fun remove() {
        component.removePort(this)
    }

    fun destroy() {
        simulation.removeNetworkNode(node)
    }
}
