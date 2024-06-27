package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.FieldInitializationContext
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider

internal typealias FieldStateMap = Map<String, Any?>

internal typealias MutableFieldStateMap = MutableMap<String, Any?>

internal interface FieldStateStorage : FieldStateProvider {

    override val variableProvider: VariableProvider

    val data: FieldStateMap

    fun copy(): FieldStateStorage

    fun mutableCopy(): MutableFieldStateStorage

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(field: Field<T>): T {
        // Check that a field with the given name exists.
        require(variableProvider.hasField(field)) { "Unknown field ${field.name}" }

        // Check that the field has been initialized.
        require(field.name in data) { "Usage of uninitialized field ${field.name}" }

        // Return the value of the field.
        return data[field.name] as T
    }

    fun <T> hasValue(field: Field<T>): Boolean {
        return field.name in data
    }

    fun isComplete(): Boolean {
        return variableProvider.fields().all { hasValue(it) }
    }
}

internal class MutableFieldStateStorage(
    override val variableProvider: VariableProvider,
    override val data: MutableFieldStateMap,
) : FieldStateStorage, MutableFieldStateProvider {

    override fun copy(): FieldStateStorage = mutableCopy()

    override fun mutableCopy(): MutableFieldStateStorage {
        return MutableFieldStateStorage(variableProvider, data.toMutableMap())
    }

    override fun <T> set(field: Field<T>, value: T) {
        // Check that a field with the given name exists.
        require(variableProvider.hasField(field)) { "Unknown field ${field.name}" }

        // Check that the value is valid for the specified field.
        require(field.isValidTypeAndValue(value)) { "Value $field does not meet the requirements for the field ${field.name}" }

        // Update the value of the field.
        data[field.name] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun update(data: FieldStateMap) {
        for ((fieldName, value) in data) {
            // Check that only existing fields are specified.
            val field = variableProvider.field(fieldName) as Field<Any?>?
                ?: throw IllegalArgumentException("Unknown field '$fieldName'")

            // Update the value of the field.
            this[field] = value
        }
    }
}

internal fun fieldStateStorage(variableProvider: VariableProvider, data: FieldStateMap): FieldStateStorage {
    return mutableFieldStateStorage(variableProvider, data)
}

internal fun fieldStateStorage(variableProvider: VariableProvider, dataProvider: FieldStateProvider): FieldStateStorage {
    return mutableFieldStateStorage(variableProvider, dataProvider)
}

internal fun mutableFieldStateStorage(variableProvider: VariableProvider, data: FieldStateMap): MutableFieldStateStorage {
    val processedData: MutableFieldStateMap = mutableMapOf()
    for (field in variableProvider.fields()) {
        // Check that the field has an assigned value.
        if (field.name !in data) {
            continue
        }

        // Get the value from the map.
        val value = data[field.name]

        // Check that the assigned value is valid for the given field. (Allow null for uninitialized fields).
        if (value != null) {
            require(field.isValidTypeAndValue(value)) { "Invalid value for the field ${field.name}" }
        }

        // Put value into map.
        processedData[field.name] = value
    }
    return MutableFieldStateStorage(variableProvider, processedData)
}

internal fun mutableFieldStateStorage(variableProvider: VariableProvider, dataProvider: FieldStateProvider): MutableFieldStateStorage {
    val processedData: MutableFieldStateMap = mutableMapOf()
    for (field in variableProvider.fields()) {
        // Check that the field has an assigned value.
        if (!dataProvider.variableProvider.hasVariable(field)) {
            continue
        }
        if (dataProvider is FieldStateStorage && !dataProvider.hasValue(field)) {
            continue
        }
        if (dataProvider is FieldStateStorageWrapper && !dataProvider.hasValue(field)) {
            continue
        }

        // Get the value from the provider.
        val value = dataProvider[field]

        // Check that the assigned value is valid for the given field. (Allow null for uninitialized fields).
        if (value != null) {
            require(field.isValidTypeAndValue(value)) { "Invalid value for the field ${field.name}" }
        }

        // Put value into map.
        processedData[field.name] = value
    }
    return MutableFieldStateStorage(variableProvider, processedData)
}

internal fun generateInitialFieldStateStorage(
    kernelVariant: KernelVariantImpl,
    settingStateProvider: SettingStateProvider,
): MutableFieldStateStorage {
    val fieldInitializationContext = FieldInitializationContext(kernelVariant, settingStateProvider)
    val fieldStates: MutableFieldStateMap = mutableMapOf()
    for (field in kernelVariant.fields()) {
        val initialization = field.initialization ?: continue
        fieldStates[field.name] = initialization.invoke(fieldInitializationContext)
    }
    return mutableFieldStateStorage(kernelVariant, fieldStates)
}
