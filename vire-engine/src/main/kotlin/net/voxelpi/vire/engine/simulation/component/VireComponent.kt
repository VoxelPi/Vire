package net.voxelpi.vire.engine.simulation.component

import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.component.ComponentPort
import net.voxelpi.vire.api.simulation.component.ComponentPortVariableView
import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineInput
import net.voxelpi.vire.api.simulation.component.StateMachineOutput
import net.voxelpi.vire.engine.simulation.VireSimulation
import net.voxelpi.vire.engine.simulation.VireSimulationObject
import java.util.UUID

class VireComponent(
    override val simulation: VireSimulation,
    override val stateMachine: StateMachine,
    override val uniqueId: UUID = UUID.randomUUID(),
) : VireSimulationObject(), Component {

    override val stateMachineContext: VireStateMachineContext = VireStateMachineContext(stateMachine)

    private val ports: MutableMap<UUID, VireComponentPort> = mutableMapOf()

    override fun ports(): Collection<VireComponentPort> {
        return ports.values
    }

    fun tick() {
        stateMachineContext.initializeOutputs()
        stateMachine.tick(stateMachineContext)
    }

    fun pullInputs() {
        for (port in ports.values) {
            val (variable, index) = port.variableView ?: continue
            if (variable is StateMachineInput) {
                stateMachineContext.pullInput(variable, index, port.node.network.state)
            }
        }
    }

    fun pushOutputs() {
        for (port in ports.values) {
            val (variable, index) = port.variableView ?: continue
            if (variable is StateMachineOutput) {
                port.network.state = stateMachineContext.pushOutput(variable, index, port.network.state)
            }
        }
    }

    fun registerPort(port: VireComponentPort) {
        ports[port.uniqueId] = port
    }

    fun unregisterPort(port: VireComponentPort) {
        ports.remove(port.uniqueId)
    }

    override fun createPort(variableView: ComponentPortVariableView?): VireComponentPort {
        val port = VireComponentPort(this, variableView)
        registerPort(port)
        return port
    }

    override fun removePort(port: ComponentPort) {
        port.remove()
    }

    override fun remove() {
        for (port in ports.values) {
            port.node.remove()
        }
        simulation.unregisterComponent(this)
    }
}
