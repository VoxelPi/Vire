package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProviderWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProviderWrapper
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A kernel setting, they allow the configuration of non-fundamental properties of a kernel, like implementation behavior.
 * The value of a setting can only be set during the creation of a kernel instance and remains immutable after that.
 */
public data class Setting<T> internal constructor(
    override val name: String,
    override val type: KType,
    public val initialization: SettingInitialization<T>?,
    override val constraint: VariableConstraint<T>,
    override val description: String,
) : ScalarVariable<T>, VariantVariable<T>, ConstrainedVariable<T>

/**
 * The setting initialization type.
 */
public typealias SettingInitialization<T> = SettingInitializationContext.() -> T

/**
 * The initialization context of a kernel setting.
 */
public class SettingInitializationContext internal constructor(
    override val variableProvider: VariableProvider,
    override val vectorSizeProvider: VectorSizeProvider,
    override val parameterStateProvider: ParameterStateProvider,
) : VariableProvider, ParameterStateProviderWrapper, VectorSizeProviderWrapper {

    override fun variables(): Collection<Variable<*>> = variableProvider.variables()

    override fun variable(name: String): Variable<*>? = variableProvider.variable(name)
}

/**
 * A builder for a kernel setting.
 *
 * @property name The name of the setting.
 * @property type The type of the setting.
 */
public class SettingBuilder<T> internal constructor(
    public val name: String,
    public val type: KType,
) {

    /**
     * The initialization of the setting.
     */
    public var initialization: SettingInitialization<T>? = null

    /**
     * The constraint of the setting.
     * The default value is [VariableConstraint.Always].
     */
    public var constraint: VariableConstraint<T> = VariableConstraint.Always

    /**
     * The description of the parameter.
     */
    public var description: String = ""

    @Suppress("UNCHECKED_CAST")
    internal fun buildInitialization(): SettingInitialization<T>? {
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
 * Creates a new setting with the given [name] and type [T] using the given [lambda].
 */
public inline fun <reified T> createSetting(name: String, noinline lambda: SettingBuilder<T>.() -> Unit = {}): Setting<T> {
    return createSetting(name, typeOf<T>(), lambda)
}

/**
 * Creates a new setting with the given [name] and [type] using the given [lambda].
 */
public fun <T> createSetting(name: String, type: KType, lambda: SettingBuilder<T>.() -> Unit = {}): Setting<T> {
    val builder = SettingBuilder<T>(name, type)
    builder.lambda()
    return Setting(name, type, builder.buildInitialization(), builder.constraint, builder.description)
}
