package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.KernelVariantWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public data class Field<T> internal constructor(
    override val name: String,
    override val type: KType,
    public val initialization: FieldInitializationContext.() -> T,
) : ScalarVariable<T>, VariantVariable<T>

public interface FieldInitializationContext : VariableProvider, ParameterStateProvider, SettingStateProvider, VectorSizeProvider {
    public val kernelVariant: KernelVariant
}

internal class FieldInitializationContextImpl(
    override val kernelVariant: KernelVariant,
    override val settingStateProvider: SettingStateProvider,
) : FieldInitializationContext, KernelVariantWrapper, SettingStateProviderWrapper {

    override fun variables(): Collection<Variable<*>> = kernelVariant.variables()

    override fun variable(name: String): Variable<*>? = kernelVariant.variable(name)

    override val variableProvider: VariableProvider
        get() = kernelVariant
}

/**
 * Creates a new field with the given [name] that is initialized to the value provided by [initialization].
 */
public inline fun <reified T> createField(
    name: String,
    noinline initialization: FieldInitializationContext.() -> T,
): Field<T> {
    return createField(name, typeOf<T>(), initialization)
}

/**
 * Creates a new field with the given [name] that is initialized to the value provided by [initialization].
 */
public fun <T> createField(
    name: String,
    type: KType,
    initialization: FieldInitializationContext.() -> T,
): Field<T> {
    return Field(name, type, initialization)
}
