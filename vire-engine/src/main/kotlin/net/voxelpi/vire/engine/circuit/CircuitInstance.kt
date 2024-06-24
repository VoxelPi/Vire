package net.voxelpi.vire.engine.circuit

import net.voxelpi.vire.engine.circuit.component.Component
import net.voxelpi.vire.engine.circuit.component.ComponentConfiguration
import net.voxelpi.vire.engine.kernel.KernelInstance
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
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

    fun initialize(circuit: CircuitImpl, circuitSettingStates: SettingStateProvider): Result<Unit> {
        componentInstances.clear()

        for (component in circuit.components()) {
            // Build setting states for the kernel of the component.
            val settings = component.configuration.settingEntries.mapValues { (_, value) ->
                when (value) {
                    is ComponentConfiguration.Entry.CircuitSetting -> circuitSettingStates[value.setting]
                    is ComponentConfiguration.Entry.Value -> value.value
                }
            }

            // Create the instance of the component kernel.
            val instance = component.kernelVariant.createInstance(settings).getOrElse {
                return Result.failure(it)
            }
            componentInstances[component.uniqueId] = instance
        }
        return Result.success(Unit)
    }

    companion object {
        fun createEmpty(): MutableCircuitInstanceImpl {
            return MutableCircuitInstanceImpl(mutableMapOf())
        }
    }
}
