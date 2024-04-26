package net.voxelpi.vire.engine.circuit.component

import net.voxelpi.event.post
import net.voxelpi.vire.engine.circuit.CircuitElement
import net.voxelpi.vire.engine.circuit.CircuitElementImpl
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.circuit.event.component.ComponentPortCreateEvent
import net.voxelpi.vire.engine.circuit.event.component.ComponentPortDestroyEvent
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelImpl
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.variable.IOVectorElement
import java.util.UUID

public interface Component : CircuitElement {

    /**
     * The kernel of the component.
     */
    public val kernel: Kernel

    /**
     * The kernel configuration of the component.
     */
    public val kernelVariant: KernelVariant

    /**
     * Returns all ports of this component.
     */
    public fun ports(): Collection<ComponentPort>

    /**
     * Returns the port of the component with the given [uniqueId].
     */
    public fun port(uniqueId: UUID): ComponentPort?

    /**
     * Creates a new component port that has the given [variable] assigned to it.
     */
    public fun createPort(variable: IOVectorElement?, uniqueId: UUID = UUID.randomUUID()): ComponentPort

    /**
     * Removes the given [port] from the component.
     */
    public fun removePort(port: ComponentPort)
}

internal class ComponentImpl(
    override val circuit: CircuitImpl,
    override val kernelVariant: KernelVariantImpl,
    override val uniqueId: UUID,
) : CircuitElementImpl(), Component {

    override val kernel: KernelImpl
        get() = kernelVariant.kernel

    private val ports: MutableMap<UUID, ComponentPortImpl> = mutableMapOf()

    override fun ports(): Collection<ComponentPort> {
        return ports.values
    }

    override fun port(uniqueId: UUID): ComponentPortImpl? {
        return ports[uniqueId]
    }

    override fun createPort(variable: IOVectorElement?, uniqueId: UUID): ComponentPort {
        // Create the port.
        val port = ComponentPortImpl(this, variable, uniqueId)
        registerPort(port)

        // Post event.
        circuit.eventScope.post(ComponentPortCreateEvent(port))

        // Return the created port.
        return port
    }

    override fun removePort(port: ComponentPort) {
        require(port is ComponentPortImpl)

        // Post event.
        circuit.eventScope.post(ComponentPortDestroyEvent(port))

        // Destroy the port.
        port.destroy()
        unregisterPort(port)
    }

    private fun registerPort(port: ComponentPortImpl) {
        ports[port.uniqueId] = port
    }

    private fun unregisterPort(port: ComponentPortImpl) {
        ports.remove(port.uniqueId)
    }

    override fun remove() {
        circuit.removeComponent(this)
    }

    fun destroy() {
        ports.values.forEach(ComponentPortImpl::remove)
    }
}
