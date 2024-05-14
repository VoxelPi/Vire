package net.voxelpi.vire.engine.kernel.variable.provider

import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.VariableProvider

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
