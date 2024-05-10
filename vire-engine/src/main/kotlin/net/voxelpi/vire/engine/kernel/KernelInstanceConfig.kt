package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.SettingStateMap
import net.voxelpi.vire.engine.kernel.variable.SettingStateProvider

internal data class KernelInstanceConfig(
    override val kernelVariant: KernelVariantImpl,
    override val variableStates: Map<String, Any?>,
) : SettingStateMap {

    constructor(kernelVariant: KernelVariantImpl, settingStateProvider: SettingStateProvider) :
        this(kernelVariant, kernelVariant.settings().associate { it.name to settingStateProvider[it] })

    init {
        for (settingName in variableStates.keys) {
            // Check that only existing settings are specified.
            require(kernelVariant.hasSetting(settingName)) { "Specified value for unknown setting '$settingName'" }
        }
        for (setting in kernelVariant.settings()) {
            // Check that every setting has an assigned value.
            require(setting.name in variableStates) { "No value for the setting ${setting.name}" }
            // Check that the assigned value is valid for the given setting.
            require(setting.isValidTypeAndValue(variableStates[setting.name])) { "Invalid value for the setting ${setting.name}" }
        }
    }
}
