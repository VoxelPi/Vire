package net.voxelpi.vire.engine.circuit.component

import net.voxelpi.vire.engine.circuit.component.port.ComponentPort
import net.voxelpi.vire.engine.circuit.kernel.variable.IOVectorElement

public interface MutableComponent : Component {

    /**
     * Creates a new component port that has the given [variable] assigned to it.
     */
    public fun createPort(variable: IOVectorElement? = null): ComponentPort

    /**
     * Removes the given [port].
     */
    public fun removePort(port: ComponentPort)
}
