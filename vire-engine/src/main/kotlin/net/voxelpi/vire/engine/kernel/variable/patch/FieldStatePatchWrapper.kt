package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialFieldStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialFieldStateProvider

internal interface FieldStatePatchWrapper : PartialFieldStateProvider {

    val fieldStatePatch: FieldStatePatch

    override val variableProvider: VariableProvider
        get() = fieldStatePatch.variableProvider

    override fun <T> get(field: Field<T>): T {
        return fieldStatePatch[field]
    }

    override fun hasValue(field: Field<*>): Boolean {
        return fieldStatePatch.hasValue(field)
    }

    override fun allFieldsSet(): Boolean {
        return fieldStatePatch.allFieldsSet()
    }
}

internal interface MutableFieldStateStorageWrapper : FieldStatePatchWrapper, MutablePartialFieldStateProvider {

    override val fieldStatePatch: MutableFieldStatePatch

    override fun <T> set(field: Field<T>, value: T) {
        fieldStatePatch[field] = value
    }
}
