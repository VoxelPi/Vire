package net.voxelpi.vire.simulation

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.Simulation
import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.event.SimulationEventService
import net.voxelpi.vire.api.simulation.network.Network
import net.voxelpi.vire.api.simulation.network.NetworkNode
import net.voxelpi.vire.api.simulation.network.NetworkState
import java.util.UUID

class VireSimulation : Simulation {
    override val eventService: SimulationEventService
        get() = TODO("Not yet implemented")

    override fun stateMachines(): Collection<StateMachine> {
        TODO("Not yet implemented")
    }

    override fun stateMachine(identifier: Identifier): StateMachine? {
        TODO("Not yet implemented")
    }

    override fun components(): Collection<Component> {
        TODO("Not yet implemented")
    }

    override fun component(uniqueId: UUID): Component? {
        TODO("Not yet implemented")
    }

    override fun createComponent(stateMachine: StateMachine): Component {
        TODO("Not yet implemented")
    }

    override fun networks(): Collection<Network> {
        TODO("Not yet implemented")
    }

    override fun network(uniqueId: UUID): Network? {
        TODO("Not yet implemented")
    }

    override fun createNetwork(uniqueId: UUID, state: NetworkState): Network {
        TODO("Not yet implemented")
    }

    override fun networkNode(uniqueId: UUID): NetworkNode? {
        TODO("Not yet implemented")
    }

    override fun createNetworkNode(network: Network, uniqueId: UUID): NetworkNode {
        TODO("Not yet implemented")
    }

    override fun createNetworkNode(connectedTo: Collection<NetworkNode>, uniqueId: UUID): NetworkNode {
        TODO("Not yet implemented")
    }

    override fun simulateSteps(numberOfSteps: Int) {
        TODO("Not yet implemented")
    }
}
