package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.FieldInitializationContext
import net.voxelpi.vire.engine.kernel.variable.UninitializedVariableException
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.FieldStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.FieldStateStorage
import net.voxelpi.vire.engine.kernel.variable.storage.MutableFieldStateMap

internal open class FieldStatePatch(
    final override val variableProvider: VariableProvider,
    initialData: FieldStateMap,
) : PartialFieldStateProvider {

    init {
        for ((fieldName, fieldState) in initialData) {
            val field = variableProvider.field(fieldName)
                ?: throw IllegalStateException("Data specified for unknown field \"$fieldName\".")

            require(field.isValidTypeAndValue(fieldState)) { "Invalid value specified for field \"$fieldName\"." }
        }
    }

    protected open val data: FieldStateMap = initialData.toMap()

    constructor(variableProvider: VariableProvider, initialData: PartialFieldStateProvider) : this(
        variableProvider,
        variableProvider.fields().filter { initialData.hasValue(it) }.associate { it.name to initialData[it] }
    )

    fun copy(): FieldStatePatch {
        return FieldStatePatch(variableProvider, data)
    }

    fun mutableCopy(): MutableFieldStatePatch {
        return MutableFieldStatePatch(variableProvider, data)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(field: Field<T>): T {
        // Check that a field with the given name exists.
        require(variableProvider.hasField(field)) { "Unknown field ${field.name}" }

        // Check that the field has been initialized.
        if (field.name !in data) {
            throw UninitializedVariableException(field)
        }

        // Return the value of the field.
        return data[field.name] as T
    }

    override fun hasValue(field: Field<*>): Boolean {
        return field.name in data
    }

    override fun allFieldsSet(): Boolean {
        return variableProvider.fields().all { hasValue(it) }
    }

    /**
     * Creates a field state storage using the set data.
     * All fields must have a set value otherwise this operation fails.
     */
    fun createStorage(): FieldStateStorage {
        return FieldStateStorage(variableProvider, data)
    }
}

internal class MutableFieldStatePatch(
    variableProvider: VariableProvider,
    initialData: FieldStateMap,
) : FieldStatePatch(variableProvider, initialData), MutablePartialFieldStateProvider {

    override val data: MutableFieldStateMap = initialData.toMutableMap()

    constructor(variableProvider: VariableProvider, initialData: PartialFieldStateProvider) : this(
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

internal fun generateInitialFieldStatePatch(
    kernelVariant: KernelVariantImpl,
    settingStateProvider: SettingStateProvider,
): MutableFieldStatePatch {
    val fieldInitializationContext = FieldInitializationContext(kernelVariant, settingStateProvider)
    val fieldStates: MutableFieldStateMap = mutableMapOf()
    for (field in kernelVariant.fields()) {
        val initialization = field.initialization ?: continue
        fieldStates[field.name] = initialization.invoke(fieldInitializationContext)
    }
    return MutableFieldStatePatch(kernelVariant, fieldStates)
}
