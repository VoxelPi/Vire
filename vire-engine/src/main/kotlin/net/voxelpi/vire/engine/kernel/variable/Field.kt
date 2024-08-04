package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProviderWrapper
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A kernel field, they allow the kernel implementation to storage internal persistent data.
 * Their values are initialized during the initialization of a kernel instance and can be read & modified in kernel updates.
 */
public data class Field<T> internal constructor(
    override val name: String,
    override val type: KType,
    public val initialization: FieldInitialization<T>?,
) : ScalarVariable<T>, VariantVariable<T>

/**
 * The setting initialization type.
 */
public typealias FieldInitialization<T> = FieldInitializationContext.() -> T

/**
 * The initialization context of a kernel field.
 */
public class FieldInitializationContext internal constructor(
    override val variableProvider: VariableProvider,
    override val vectorSizeProvider: VectorSizeProvider,
    override val parameterStateProvider: ParameterStateProvider,
    override val settingStateProvider: SettingStateProvider,
) : VariableProvider, VectorSizeProviderWrapper, ParameterStateProviderWrapper, SettingStateProviderWrapper {

    override fun variables(): Collection<Variable<*>> = variableProvider.variables()

    override fun variable(name: String): Variable<*>? = variableProvider.variable(name)
}

/**
 * A builder for a kernel field.
 *
 * @property name The name of the field.
 * @property type The type of the field.
 */
public class FieldBuilder<T> internal constructor(
    public val name: String,
    public val type: KType,
) {

    /**
     * The initialization of the field.
     */
    public var initialization: FieldInitialization<T>? = null

    @Suppress("UNCHECKED_CAST")
    internal fun buildInitialization(): FieldInitialization<T>? {
        // Return null initialization if the type allows it.
        if (type.isMarkedNullable) {
            return {
                null as T
            }
        }

        return initialization
    }
}

/**
 * Creates a new field with the given [name] and type [T] using the given [lambda].
 */
public inline fun <reified T> createField(name: String, noinline lambda: FieldBuilder<T>.() -> Unit = {}): Field<T> {
    return createField(name, typeOf<T>(), lambda)
}

/**
 * Creates a new field with the given [name] and [type] using the given [lambda].
 */
public fun <T> createField(name: String, type: KType, lambda: FieldBuilder<T>.() -> Unit = {}): Field<T> {
    val builder = FieldBuilder<T>(name, type)
    builder.lambda()
    return Field(name, type, builder.buildInitialization())
}
