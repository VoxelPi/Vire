package net.voxelpi.vire.engine.circuit.component

import net.voxelpi.event.post
import net.voxelpi.vire.api.circuit.component.Component
import net.voxelpi.vire.api.circuit.component.ComponentPort
import net.voxelpi.vire.api.circuit.component.ComponentPortVectorVariable
import net.voxelpi.vire.api.circuit.event.component.ComponentConfigureEvent
import net.voxelpi.vire.api.circuit.event.component.port.ComponentPortCreateEvent
import net.voxelpi.vire.api.circuit.event.component.port.ComponentPortDestroyEvent
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.StateMachineInput
import net.voxelpi.vire.api.circuit.statemachine.StateMachineOutput
import net.voxelpi.vire.engine.circuit.VireCircuit
import net.voxelpi.vire.engine.circuit.VireCircuitElement
import net.voxelpi.vire.engine.circuit.statemachine.VireStateMachineInstance
import java.util.UUID

class VireComponent(
    override val circuit: VireCircuit,
    override val stateMachineInstance: VireStateMachineInstance,
    override val uniqueId: UUID = UUID.randomUUID(),
) : VireCircuitElement(), Component {

    override val stateMachine: StateMachine
        get() = stateMachineInstance.stateMachine

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
