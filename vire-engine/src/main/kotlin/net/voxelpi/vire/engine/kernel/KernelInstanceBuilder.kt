package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.provider.MutableSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.MutableSettingStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableSettingStateStorageWrapper
import net.voxelpi.vire.engine.kernel.variable.storage.mutableSettingStateStorage

/**
 * A builder for a kernel instance.
 */
public interface KernelInstanceBuilder : MutableSettingStateProvider {

    /**
     * The kernel which of which the instance should be created.
     */
    public val kernel: Kernel
        get() = kernelVariant.kernel

    /**
     * The kernel variant which of which the instance should be created.
     */
    public val kernelVariant: KernelVariant

    /**
     * Returns the current value of the setting with the given [settingName].
     *
     * @param settingName the name of the setting of which the value should be returned.
     */
    public operator fun get(settingName: String): Any?

    /**
     * Sets the value of the setting with the given [settingName] to the given [value].
     *
     * @param settingName the name of the setting of which the value should be modified.
     * @param value the new value of the setting.
     */
    public operator fun set(settingName: String, value: Any?)
}

internal class KernelInstanceBuilderImpl(
    override val kernelVariant: KernelVariantImpl,
    override val settingStateStorage: MutableSettingStateStorage,
) : KernelInstanceBuilder, MutableSettingStateStorageWrapper {

    constructor(kernelVariant: KernelVariantImpl, settingStateProvider: SettingStateProvider) :
        this(kernelVariant, mutableSettingStateStorage(kernelVariant, settingStateProvider))

    override fun get(settingName: String): Any? {
        // Check that a setting with the given name exists.
        val setting = kernel.setting(settingName)
            ?: throw IllegalArgumentException("Unknown setting '$settingName'")

        // Return the value of the setting.
        return settingStateStorage[setting]
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(settingName: String, value: Any?) {
        // Check that a setting with the given name exists.
        val setting = kernel.setting(settingName) as Setting<Any?>?
            ?: throw IllegalArgumentException("Unknown setting '$settingName'")

        // Update the value of the setting.
        settingStateStorage[setting] = value
    }

    fun apply(values: Map<String, Any?>): KernelInstanceBuilderImpl {
        settingStateStorage.update(values)
        return this
    }

    fun build(): KernelInstanceConfig {
        return KernelInstanceConfig(kernelVariant, settingStateStorage.copy())
    }
}
