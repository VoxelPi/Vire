package net.voxelpi.vire.simulation.component

import net.voxelpi.vire.api.simulation.component.ComponentPort
import net.voxelpi.vire.api.simulation.component.ComponentPortVariableView
import net.voxelpi.vire.api.simulation.component.StateMachineOutput
import net.voxelpi.vire.api.simulation.network.NetworkState
import net.voxelpi.vire.simulation.VireSimulation
import net.voxelpi.vire.simulation.network.VireNetwork
import net.voxelpi.vire.simulation.network.VireNetworkNode
import java.util.UUID

class VireComponentPort(
    override val component: VireComponent,
    override var variableView: ComponentPortVariableView?,
    override val uniqueId: UUID = UUID.randomUUID(),
) : ComponentPort {

    override val simulation: VireSimulation
        get() = component.simulation

    override val node: VireNetworkNode = simulation.createNetworkNode(simulation.createNetwork(), uniqueId)

    override var network: VireNetwork
        get() = node.network
        set(value) { node.network = value }

    override fun pushOutput(): NetworkState? {
        val access = variableView ?: return null

        val (variable, index) = access
        if (variable !is StateMachineOutput) {
            return null
        }

        return component.stateMachineContext.pushOutput(variable, index, network.state)
    }
}
