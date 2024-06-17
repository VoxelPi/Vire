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

public class FieldInitializationContext internal constructor(
    override val kernelVariant: KernelVariant,
    override val settingStateProvider: SettingStateProvider,
) : VariableProvider, ParameterStateProvider, VectorSizeProvider, KernelVariantWrapper, SettingStateProviderWrapper {

    override fun variables(): Collection<Variable<*>> = kernelVariant.variables()

    override fun variable(name: String): Variable<*>? = kernelVariant.variable(name)

    override val variableProvider: VariableProvider
        get() = kernelVariant
}

public class FieldBuilder<T> internal constructor(
    public val name: String,
    public val type: KType,
) {

    public lateinit var initialization: FieldInitializationContext.() -> T

    @Suppress("UNCHECKED_CAST")
    internal fun buildInitialization(): FieldInitializationContext.() -> T {
        // Check if the initialization has been set.
        val initialized = ::initialization.isInitialized
        if (initialized) {
            return initialization
        }

        // Return null initialization if the type allows it.
        if (type.isMarkedNullable) {
            return {
                null as T
            }
        }

        // Otherwise throw.
        throw IllegalArgumentException("Missing initialization for field \"$name\"")
    }
}

public inline fun <reified T> createField(name: String, noinline lambda: FieldBuilder<T>.() -> Unit = {}): Field<T> {
    return createField(name, typeOf<T>(), lambda)
}

public fun <T> createField(name: String, type: KType, lambda: FieldBuilder<T>.() -> Unit = {}): Field<T> {
    val builder = FieldBuilder<T>(name, type)
    builder.lambda()
    return Field(name, type, builder.buildInitialization())
}
