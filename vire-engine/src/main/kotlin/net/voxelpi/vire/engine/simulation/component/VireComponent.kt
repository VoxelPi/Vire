package net.voxelpi.vire.engine.simulation.component

import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.component.ComponentPort
import net.voxelpi.vire.api.simulation.component.ComponentPortVectorVariable
import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineInput
import net.voxelpi.vire.api.simulation.component.StateMachineOutput
import net.voxelpi.vire.api.simulation.component.StateMachineParameter
import net.voxelpi.vire.api.simulation.event.simulation.component.ComponentConfigureEvent
import net.voxelpi.vire.api.simulation.event.simulation.component.port.ComponentPortCreateEvent
import net.voxelpi.vire.api.simulation.event.simulation.component.port.ComponentPortDestroyEvent
import net.voxelpi.vire.engine.simulation.VireSimulation
import net.voxelpi.vire.engine.simulation.VireSimulationObject
import java.util.UUID

class VireComponent(
    override val simulation: VireSimulation,
    override val stateMachine: StateMachine,
    override val uniqueId: UUID = UUID.randomUUID(),
) : VireSimulationObject(), Component {

    override val stateMachineContext: VireStateMachineContext = VireStateMachineContext(this)

    private val ports: MutableMap<UUID, VireComponentPort> = mutableMapOf()

    override fun <T> parameter(parameter: StateMachineParameter<T>): T {
        return stateMachineContext[parameter]
    }

    override fun <T> parameter(parameter: StateMachineParameter<T>, value: T): Boolean {
        // Check that the new value satisfies the predicate of the parameter.
        if (!parameter.isValid(value, stateMachineContext)) {
            return false
        }

        // Set the value of the parameter.
        stateMachineContext[parameter] = value
        return true
    }

    override fun ports(): List<VireComponentPort> {
        return ports.values.toList()
    }

    fun tick() {
        stateMachineContext.initializeOutputs()
        stateMachine.tick(stateMachineContext)
    }

    fun pullInputs() {
        stateMachineContext.initializeInputs()
        for (port in ports.values) {
            val (vector, index) = port.variable ?: continue
            if (vector is StateMachineInput) {
                stateMachineContext.pullInput(vector, index, port.node.network.state)
            }
        }
    }

    fun pushOutputs() {
        for (port in ports.values) {
            val (vector, index) = port.variable ?: continue
            if (vector is StateMachineOutput) {
                port.network.state = stateMachineContext.pushOutput(vector, index, port.network.state)
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
        stateMachineContext.reset(parameters)
        simulation.publish(ComponentConfigureEvent(this))
    }
}
