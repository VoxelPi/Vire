package net.voxelpi.vire.engine.circuit

import net.voxelpi.vire.engine.circuit.component.Component
import net.voxelpi.vire.engine.circuit.component.ComponentConfiguration
import net.voxelpi.vire.engine.kernel.KernelInstance
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.patch.MutableSettingStatePatch
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
        @Suppress("UNCHECKED_CAST")
        fun circuitInstance(circuit: CircuitImpl, settingStates: SettingStateProvider): Result<CircuitInstanceImpl> {
            val settingStateStorage = SettingStateStorage(circuit, settingStates)

            val componentInstances = mutableMapOf<UUID, KernelInstance>()
            for (component in circuit.components()) {
                // Build setting states for the kernel of the component.
                val settings = MutableSettingStatePatch(component.kernelVariant)
                for ((settingName, settingValue) in component.configuration.settingEntries) {
                    val setting = component.kernelVariant.setting(settingName)!!

                    settings[setting as Setting<Any?>] = when (settingValue) {
                        is ComponentConfiguration.Entry.CircuitSetting -> settingStates[settingValue.setting]
                        is ComponentConfiguration.Entry.Value -> settingValue.value
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
