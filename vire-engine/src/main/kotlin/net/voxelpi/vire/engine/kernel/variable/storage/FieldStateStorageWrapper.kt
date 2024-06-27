package net.voxelpi.vire.engine.kernel.variable.storage

import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.FieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableFieldStateProvider

internal interface FieldStateStorageWrapper : FieldStateProvider {

    val fieldStateStorage: FieldStateStorage

    override val variableProvider: VariableProvider
        get() = fieldStateStorage.variableProvider

    override fun <T> get(field: Field<T>): T {
        return fieldStateStorage[field]
    }

    fun <T> hasValue(field: Field<T>): Boolean {
        return fieldStateStorage.hasValue(field)
    }
}

internal interface MutableFieldStateStorageWrapper : FieldStateStorageWrapper, MutableFieldStateProvider {

    override val fieldStateStorage: MutableFieldStateStorage

    override fun <T> set(field: Field<T>, value: T) {
        fieldStateStorage[field] = value
    }
}
