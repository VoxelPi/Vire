package net.voxelpi.vire.engine.simulation.component

import net.voxelpi.event.post
import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.component.ComponentPort
import net.voxelpi.vire.api.simulation.component.ComponentPortVectorVariable
import net.voxelpi.vire.api.simulation.event.component.ComponentConfigureEvent
import net.voxelpi.vire.api.simulation.event.component.port.ComponentPortCreateEvent
import net.voxelpi.vire.api.simulation.event.component.port.ComponentPortDestroyEvent
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineOutput
import net.voxelpi.vire.engine.simulation.VireCircuit
import net.voxelpi.vire.engine.simulation.VireCircuitElement
import net.voxelpi.vire.engine.simulation.statemachine.VireStateMachine
import net.voxelpi.vire.engine.simulation.statemachine.VireStateMachineInstance
import java.util.UUID

class VireComponent(
    override val circuit: VireCircuit,
    override val stateMachine: VireStateMachine,
    override val uniqueId: UUID = UUID.randomUUID(),
) : VireCircuitElement(), Component {

    override val stateMachineInstance: VireStateMachineInstance = stateMachine.createInstance {}

    private val ports: MutableMap<UUID, VireComponentPort> = mutableMapOf()

    init {
        stateMachineInstance.configurationCallback = {
            circuit.eventScope.post(ComponentConfigureEvent(this))
        }
    }

    override fun ports(): List<VireComponentPort> {
        return ports.values.toList()
    }

    fun tick() {
        stateMachineInstance.initializeOutputs()
        stateMachineInstance.update()
    }

    fun pullInputs() {
        stateMachineInstance.initializeInputs()
        for (port in ports.values) {
            val (vector, index) = port.variable ?: continue
            if (vector is StateMachineInput) {
                stateMachineInstance.pullInput(vector, index, port.node.network.state)
            }
        }
    }

    fun pushOutputs() {
        for (port in ports.values) {
            val (vector, index) = port.variable ?: continue
            if (vector is StateMachineOutput) {
                port.network.state = stateMachineInstance.pushOutput(vector, index, port.network.state)
            }
        }
    }

    override fun createPort(variable: ComponentPortVectorVariable?): VireComponentPort {
        // Create the port.
        val port = VireComponentPort(this, variable)
        ports[port.uniqueId] = port

        // Fire the event.
        circuit.eventScope.post(ComponentPortCreateEvent(port))

        // Return the created port.
        return port
    }

    override fun removePort(port: ComponentPort) {
        require(port is VireComponentPort)

        // Fire the event.
        circuit.eventScope.post(ComponentPortDestroyEvent(port))

        // Destroy the port.
        ports.remove(port.uniqueId)
        port.destroy()
    }

    override fun remove() {
        circuit.removeComponent(this)
    }

    fun destroy() {
        ports.values.forEach(VireComponentPort::destroy)
    }

    override fun reset(parameters: Boolean) {
        stateMachineInstance.reset(parameters)
    }
}
