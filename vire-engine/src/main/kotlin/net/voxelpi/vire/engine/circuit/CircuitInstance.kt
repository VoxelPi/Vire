package net.voxelpi.vire.engine.circuit

import net.voxelpi.vire.engine.circuit.component.Component
import net.voxelpi.vire.engine.kernel.KernelInstance
import java.util.UUID

public interface CircuitInstance {

    public operator fun get(component: Component): KernelInstance
}

public interface MutableCircuitInstance : CircuitInstance {

    public operator fun set(component: Component, instance: KernelInstance)
}

internal class MutableCircuitInstanceImpl(
    val componentInstances: MutableMap<UUID, KernelInstance>,
) : MutableCircuitInstance {

    override fun get(component: Component): KernelInstance {
        return componentInstances[component.uniqueId]!!
    }

    override fun set(component: Component, instance: KernelInstance) {
        componentInstances[component.uniqueId] = instance
    }
}
