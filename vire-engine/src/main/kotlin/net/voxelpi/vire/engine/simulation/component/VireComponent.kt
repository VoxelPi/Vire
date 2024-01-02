package net.voxelpi.vire.engine.simulation.component

import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.component.ComponentPort
import net.voxelpi.vire.api.simulation.component.ComponentPortVectorVariable
import net.voxelpi.vire.api.simulation.event.simulation.component.ComponentConfigureEvent
import net.voxelpi.vire.api.simulation.event.simulation.component.port.ComponentPortCreateEvent
import net.voxelpi.vire.api.simulation.event.simulation.component.port.ComponentPortDestroyEvent
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineInput
import net.voxelpi.vire.api.simulation.statemachine.StateMachineOutput
import net.voxelpi.vire.engine.simulation.VireSimulation
import net.voxelpi.vire.engine.simulation.VireSimulationObject
import net.voxelpi.vire.engine.simulation.statemachine.VireStateMachineInstance
import java.util.UUID

class VireComponent(
    override val simulation: VireSimulation,
    override val stateMachine: StateMachine,
    override val uniqueId: UUID = UUID.randomUUID(),
) : VireSimulationObject(), Component {

    override val stateMachineInstance: VireStateMachineInstance = VireStateMachineInstance(this)

    private val ports: MutableMap<UUID, VireComponentPort> = mutableMapOf()

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
        simulation.publish(ComponentPortCreateEvent(port))

        // Return the created port.
        return port
    }

    override fun removePort(port: ComponentPort) {
        require(port is VireComponentPort)

        // Fire the event.
        simulation.publish(ComponentPortDestroyEvent(port))

        // Destroy the port.
        ports.remove(port.uniqueId)
        port.destroy()
    }

    override fun remove() {
        simulation.removeComponent(this)
    }

    fun destroy() {
        ports.values.forEach(VireComponentPort::destroy)
    }

    override fun reset(parameters: Boolean) {
        stateMachineInstance.reset(parameters)
        simulation.publish(ComponentConfigureEvent(this))
    }
}
