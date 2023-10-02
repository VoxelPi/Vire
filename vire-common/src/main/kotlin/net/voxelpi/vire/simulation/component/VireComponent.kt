package net.voxelpi.vire.simulation.component

import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineInput
import net.voxelpi.vire.api.simulation.component.StateMachineOutput
import net.voxelpi.vire.simulation.VireSimulation
import java.util.UUID

class VireComponent(
    override val simulation: VireSimulation,
    override val stateMachine: StateMachine,
    override val uniqueId: UUID = UUID.randomUUID(),
) : Component {

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
        stateMachineContext.initializeInputs()
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
}
