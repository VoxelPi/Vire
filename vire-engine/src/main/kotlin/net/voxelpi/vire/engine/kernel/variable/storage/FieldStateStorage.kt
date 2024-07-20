package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableFieldStateProvider

internal typealias FieldStateMap = Map<String, Any?>

internal typealias MutableFieldStateMap = MutableMap<String, Any?>

internal open class FieldStateStorage(
    final override val variableProvider: VariableProvider,
    initialData: FieldStateMap,
) : FieldStateProvider {

    init {
        for ((fieldName, fieldState) in initialData) {
            val field = variableProvider.field(fieldName)
                ?: throw IllegalStateException("Data specified for unknown field \"$fieldName\".")

            require(field.isValidTypeAndValue(fieldState)) { "Invalid value specified for field \"$fieldName\"." }
        }

        val missingVariables = variableProvider.fields().map { it.name }.filter { it !in initialData }
        require(missingVariables.isEmpty()) {
            "Missing values for the following fields: ${missingVariables.joinToString(", ") { "\"${it}\"" } }"
        }
    }

    protected open val data: FieldStateMap = initialData.toMap()

    constructor(variableProvider: VariableProvider, initialData: FieldStateProvider) : this(
        variableProvider,
        variableProvider.fields().filter { initialData.hasValue(it) }.associate { it.name to initialData[it] }
    )

    fun copy(): FieldStateStorage {
        return FieldStateStorage(variableProvider, data)
    }

    fun mutableCopy(): MutableFieldStateStorage {
        return MutableFieldStateStorage(variableProvider, data)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(field: Field<T>): T {
        // Check that a field with the given name exists.
        require(variableProvider.hasField(field)) { "Unknown field ${field.name}" }

        // Check that the field has been initialized.
        require(field.name in data) { "Usage of uninitialized field ${field.name}" }

        // Return the value of the field.
        return data[field.name] as T
    }
}

internal class MutableFieldStateStorage(
    variableProvider: VariableProvider,
    initialData: FieldStateMap,
) : FieldStateStorage(variableProvider, initialData), MutableFieldStateProvider {

    override val data: MutableFieldStateMap = initialData.toMutableMap()

    constructor(variableProvider: VariableProvider, initialData: FieldStateProvider) : this(
        variableProvider,
        variableProvider.fields().filter { initialData.hasValue(it) }.associate { it.name to initialData[it] }
    )

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
