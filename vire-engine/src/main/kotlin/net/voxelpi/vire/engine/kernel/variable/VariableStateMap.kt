package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.kernel.Kernel

internal interface VectorVariableSizeMap : VectorVariableSizeProvider {

    val kernel: Kernel

    val vectorVariableSizes: Map<String, Int>

    override fun size(vector: VectorVariable<*>): Int = size(vector.name)

    override fun size(vectorName: String): Int {
        // Check that the vector variable is defined on the kernel.
        require(kernel.hasVectorVariable(vectorName))

        // Return the size of the vector variable from the map.
        return vectorVariableSizes[vectorName]!!
    }
}

internal interface MutableVectorVariableSizeMap : VectorVariableSizeMap, MutableVectorVariableSizeProvider {

    override val vectorVariableSizes: MutableMap<String, Int>

    override fun resize(vector: VectorVariable<*>, size: Int) = resize(vector.name, size)

    override fun resize(vectorName: String, size: Int) {
        // Check that the vector variable is defined on the kernel.
        require(kernel.hasVectorVariable(vectorName))

        // Check that the size of the vector variable is greater than 0.
        require(size >= 0) { "The size of a vector variable must be greater than or equal to zero" }

        // Modify the size of the vector variable in the map.
        vectorVariableSizes[vectorName] = size
    }
}

internal interface SettingStateMap : SettingStateProvider {

    val kernel: Kernel

    val variableStates: Map<String, Any?>

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(setting: Setting<T>): T {
        // Check that a setting with the given name exists.
        require(kernel.hasSetting(setting.name)) { "Unknown setting ${setting.name}" }

        // Return the value of the setting.
        return variableStates[setting.name] as T
    }
}

internal interface MutableSettingStateMap : SettingStateMap, MutableSettingStateProvider {

    override val variableStates: MutableMap<String, Any?>

    override fun <T> set(setting: Setting<T>, value: T) {
        // Check that a setting with the given name exists.
        require(kernel.hasParameter(setting.name)) { "Unknown setting ${setting.name}" }

        // Check that the value is valid for the specified setting.
        require(setting.isValidValue(value)) { "Value $setting does not meet the requirements for the setting ${setting.name}" }

        // Update the value of the parameter.
        variableStates[setting.name] = value
    }
}
