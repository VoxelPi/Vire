package net.voxelpi.vire.engine.kernel

import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.patch.MutableSettingStatePatch
import net.voxelpi.vire.engine.kernel.variable.patch.MutableSettingStatePatchWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialSettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialSettingStateProvider

/**
 * A builder for a kernel instance.
 */
public interface KernelInstanceBuilder : MutablePartialSettingStateProvider {

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
    override val settingStatePatch: MutableSettingStatePatch,
) : KernelInstanceBuilder, MutableSettingStatePatchWrapper {

    constructor(kernelVariant: KernelVariantImpl, settingStateProvider: PartialSettingStateProvider) :
        this(kernelVariant, MutableSettingStatePatch(kernelVariant, settingStateProvider))

    override fun get(settingName: String): Any? {
        // Check that a setting with the given name exists.
        val setting = kernel.setting(settingName)
            ?: throw IllegalArgumentException("Unknown setting '$settingName'")

        // Return the value of the setting.
        return settingStatePatch[setting]
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(settingName: String, value: Any?) {
        // Check that a setting with the given name exists.
        val setting = kernel.setting(settingName) as Setting<Any?>?
            ?: throw IllegalArgumentException("Unknown setting '$settingName'")

        // Update the value of the setting.
        settingStatePatch[setting] = value
    }

    fun apply(values: Map<String, Any?>): KernelInstanceBuilderImpl {
        settingStatePatch.update(values)
        return this
    }

    fun build(): KernelInstanceConfig {
        return KernelInstanceConfig(kernelVariant, settingStatePatch.createStorage())
    }
}
