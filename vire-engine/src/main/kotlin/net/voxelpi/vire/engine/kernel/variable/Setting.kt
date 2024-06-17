package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.KernelVariantWrapper
import net.voxelpi.vire.engine.kernel.variable.provider.ParameterStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.VectorSizeProvider
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public data class Setting<T> internal constructor(
    override val name: String,
    override val type: KType,
    public val initialization: SettingInitializationContext.() -> T,
    override val constraint: VariableConstraint<T>,
) : ScalarVariable<T>, VariantVariable<T>, ConstrainedVariable<T>

public class SettingInitializationContext internal constructor(
    public override val kernelVariant: KernelVariant,
) : VariableProvider, ParameterStateProvider, VectorSizeProvider, KernelVariantWrapper {

    override fun variables(): Collection<Variable<*>> = kernelVariant.variables()

    override fun variable(name: String): Variable<*>? = kernelVariant.variable(name)

    override val variableProvider: VariableProvider
        get() = kernelVariant
}

public class SettingBuilder<T> internal constructor(
    public val name: String,
    public val type: KType,
) {

    public lateinit var initialization: SettingInitializationContext.() -> T

    public var constraint: VariableConstraint<T> = VariableConstraint.Always

    @Suppress("UNCHECKED_CAST")
    internal fun buildInitialization(): SettingInitializationContext.() -> T {
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
        throw IllegalArgumentException("Missing initialization for setting \"$name\"")
    }
}

public inline fun <reified T> createSetting(name: String, noinline lambda: SettingBuilder<T>.() -> Unit = {}): Setting<T> {
    return createSetting(name, typeOf<T>(), lambda)
}

public fun <T> createSetting(name: String, type: KType, lambda: SettingBuilder<T>.() -> Unit = {}): Setting<T> {
    val builder = SettingBuilder<T>(name, type)
    builder.lambda()
    return Setting(name, type, builder.buildInitialization(), builder.constraint)
}
