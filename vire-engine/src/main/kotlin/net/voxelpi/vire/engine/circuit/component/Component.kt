package net.voxelpi.vire.engine.circuit.component

import net.voxelpi.vire.engine.circuit.CircuitElement
import net.voxelpi.vire.engine.circuit.CircuitElementImpl
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.circuit.kernel.Kernel
import net.voxelpi.vire.engine.circuit.kernel.KernelImpl
import net.voxelpi.vire.engine.circuit.kernel.KernelInstance
import net.voxelpi.vire.engine.circuit.kernel.KernelInstanceImpl
import net.voxelpi.vire.engine.circuit.kernel.variable.IOVectorElement
import java.util.UUID

public interface Component : CircuitElement {

    /**
     * The kernel of the component.
     */
    public val kernel: Kernel

    /**
     * The kernel instance of the component.
     */
    public val kernelInstance: KernelInstance

    /**
     * Returns all ports of this component.
     */
    public fun ports(): Collection<ComponentPort>

    /**
     * Creates a new component port that has the given [variable] assigned to it.
     */
    public fun createPort(variable: IOVectorElement? = null): ComponentPort

    /**
     * Removes the given [port] from the component.
     */
    public fun removePort(port: ComponentPort)
}

internal class ComponentImpl(
    override val circuit: CircuitImpl,
    override val kernelInstance: KernelInstanceImpl,
    override val uniqueId: UUID,
) : CircuitElementImpl(), Component {

    override val kernel: KernelImpl
        get() = kernelInstance.kernel

    private val ports: MutableMap<UUID, ComponentPortImpl> = mutableMapOf()

    override fun ports(): Collection<ComponentPort> {
        return ports.values
    }

    override fun createPort(variable: IOVectorElement?): ComponentPort {
        TODO("Not yet implemented")
    }

    override fun removePort(port: ComponentPort) {
        TODO("Not yet implemented")
    }

    override fun remove() {
        circuit.removeComponent(this)
    }

    fun destroy() {
        TODO("Not yet implemented")
    }
}
