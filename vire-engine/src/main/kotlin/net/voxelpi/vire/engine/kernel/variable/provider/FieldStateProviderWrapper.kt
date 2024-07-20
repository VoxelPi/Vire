package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

internal interface PartialFieldStateProviderWrapper : PartialFieldStateProvider {

    val fieldStateProvider: PartialFieldStateProvider

    override val variableProvider: VariableProvider
        get() = fieldStateProvider.variableProvider

    override fun <T> get(field: Field<T>): T {
        return fieldStateProvider[field]
    }

    override fun hasValue(field: Field<*>): Boolean {
        return fieldStateProvider.hasValue(field)
    }

    override fun allFieldsSet(): Boolean {
        return fieldStateProvider.allFieldsSet()
    }
}

internal interface MutablePartialFieldStateProviderWrapper : PartialFieldStateProviderWrapper, MutablePartialFieldStateProvider {

    override val fieldStateProvider: MutablePartialFieldStateProvider

    override fun <T> set(field: Field<T>, value: T) {
        fieldStateProvider[field] = value
    }
}

internal interface FieldStateProviderWrapper : FieldStateProvider {

    val fieldStateProvider: FieldStateProvider

    override val variableProvider: VariableProvider
        get() = fieldStateProvider.variableProvider

    override fun <T> get(field: Field<T>): T {
        return fieldStateProvider[field]
    }
}

internal interface MutableFieldStateProviderWrapper : FieldStateProviderWrapper, MutableFieldStateProvider {

    override val fieldStateProvider: MutableFieldStateProvider

    override fun <T> set(field: Field<T>, value: T) {
        fieldStateProvider[field] = value
    }
}
