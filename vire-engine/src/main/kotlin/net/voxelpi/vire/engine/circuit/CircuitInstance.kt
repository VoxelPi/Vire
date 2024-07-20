package net.voxelpi.vire.engine.circuit

import net.voxelpi.vire.engine.circuit.component.Component
import net.voxelpi.vire.engine.circuit.component.ComponentConfiguration
import net.voxelpi.vire.engine.kernel.KernelInstance
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.SettingStateStorageWrapper
import java.util.UUID

public interface CircuitInstance : SettingStateProvider {

    public val circuit: Circuit

    public operator fun get(component: Component): KernelInstance

    public fun createInitialState(): MutableCircuitState
}

internal class CircuitInstanceImpl(
    override val circuit: CircuitImpl,
    override val settingStateStorage: SettingStateStorage,
    val componentInstances: Map<UUID, KernelInstance>,
) : CircuitInstance, SettingStateStorageWrapper {

    override fun get(component: Component): KernelInstance {
        return componentInstances[component.uniqueId]!!
    }

    override fun createInitialState(): MutableCircuitState {
        return MutableCircuitStateImpl.circuitState(this)
    }

    companion object {
        fun circuitInstance(circuit: CircuitImpl, settingStates: SettingStateProvider): Result<CircuitInstanceImpl> {
            val settingStateStorage = SettingStateStorage(circuit, settingStates)

            val componentInstances = mutableMapOf<UUID, KernelInstance>()
            for (component in circuit.components()) {
                // Build setting states for the kernel of the component.
                val settings = component.configuration.settingEntries.mapValues { (_, value) ->
                    when (value) {
                        is ComponentConfiguration.Entry.CircuitSetting -> settingStates[value.setting]
                        is ComponentConfiguration.Entry.Value -> value.value
                    }
                }

                // Create the instance of the component kernel.
                val instance = component.kernelVariant.createInstance(settings).getOrElse {
                    return Result.failure(it)
                }
                componentInstances[component.uniqueId] = instance
            }

            val instance = CircuitInstanceImpl(circuit, settingStateStorage, componentInstances)
            return Result.success(instance)
        }
    }
}
