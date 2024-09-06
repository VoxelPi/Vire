package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableFieldStateProvider

public typealias FieldStateMap = Map<String, Any?>

public typealias MutableFieldStateMap = MutableMap<String, Any?>

/**
 * A collection that stores the state of all fields of the given [variableProvider].
 */
public open class FieldStateStorage(
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

    /**
     * The stored data of this storage.
     */
    protected open val data: FieldStateMap = initialData.toMap()

    public constructor(variableProvider: VariableProvider, initialData: FieldStateProvider) : this(
        variableProvider,
        variableProvider.fields().filter { initialData.hasValue(it) }.associate { it.name to initialData[it] }
    )

    /**
     * Creates a copy of this storage.
     */
    public fun copy(): FieldStateStorage {
        return FieldStateStorage(variableProvider, data)
    }

    /**
     * Creates a mutable copy of this storage.
     */
    public fun mutableCopy(): MutableFieldStateStorage {
        return MutableFieldStateStorage(variableProvider, data)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(field: Field<T>): T {
        // Check that a field with the given name exists.
        require(variableProvider.hasField(field)) { "Unknown field ${field.name}" }

        // Return the value of the field.
        return data[field.name] as T
    }
}

/**
 * A mutable collection that stores the state of all fields of the given [variableProvider].
 */
public class MutableFieldStateStorage(
    variableProvider: VariableProvider,
    initialData: FieldStateMap,
) : FieldStateStorage(variableProvider, initialData), MutableFieldStateProvider {

    override val data: MutableFieldStateMap = initialData.toMutableMap()

    public constructor(variableProvider: VariableProvider, initialData: FieldStateProvider) : this(
        variableProvider,
        variableProvider.fields().filter { initialData.hasValue(it) }.associate { it.name to initialData[it] }
    )

    override fun <T> set(field: Field<T>, value: T) {
        // Check that a field with the given name exists.
        require(variableProvider.hasField(field)) { "Unknown field ${field.name}" }

        // Check that the value is valid for the specified field.
        require(field.isValidTypeAndValue(value)) { "Value $value does not meet the requirements for the field ${field.name}" }

        // Update the value of the field.
        data[field.name] = value
    }
}
