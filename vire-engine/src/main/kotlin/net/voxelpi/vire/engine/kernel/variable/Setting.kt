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

public interface SettingInitializationContext : VariableProvider, ParameterStateProvider, VectorSizeProvider {
    public val kernelVariant: KernelVariant
}

internal class SettingInitializationContextImpl(
    override val kernelVariant: KernelVariant,
) : SettingInitializationContext, KernelVariantWrapper {

    override fun variables(): Collection<Variable<*>> = kernelVariant.variables()

    override fun variable(name: String): Variable<*>? = kernelVariant.variable(name)

    override val variableProvider: VariableProvider
        get() = kernelVariant
}

/**
 * Creates a new setting with the given [name], [initialization] and [constraint].
 */
public inline fun <reified T> createSetting(
    name: String,
    noinline initialization: SettingInitializationContext.() -> T,
    constraint: VariableConstraint<T> = VariableConstraint.Always,
): Setting<T> = createSetting(name, typeOf<T>(), initialization, constraint)

/**
 * Creates a new setting with the given [name], [initialization] and [constraintBuilder].
 * The [constraintBuilder] is used to create an all-constrained, that means a value must be valid for all the defined constrains.
 */
public inline fun <reified T> createSetting(
    name: String,
    noinline initialization: SettingInitializationContext.() -> T,
    noinline constraintBuilder: AllVariableConstraintBuilder<T>.() -> Unit,
): Setting<T> = createSetting(name, typeOf<T>(), initialization, constraintBuilder)

/**
 * Creates a new setting with the given [name], [type], [initialization] and [constraint].
 */
public fun <T> createSetting(
    name: String,
    type: KType,
    initialization: SettingInitializationContext.() -> T,
    constraint: VariableConstraint<T> = VariableConstraint.Always,
): Setting<T> = Setting(name, type, initialization, constraint)

/**
 * Creates a new setting with the given [name], [type], [initialization] and [constraintBuilder].
 * The [constraintBuilder] is used to create an all-constrained, that means a value must be valid for all the defined constrains.
 */
public fun <T> createSetting(
    name: String,
    type: KType,
    initialization: SettingInitializationContext.() -> T,
    constraintBuilder: AllVariableConstraintBuilder<T>.() -> Unit,
): Setting<T> = Setting(name, type, initialization, AllVariableConstraintBuilder<T>().apply(constraintBuilder).build())
