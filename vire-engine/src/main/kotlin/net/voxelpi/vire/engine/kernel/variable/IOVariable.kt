package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.LogicState
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A kernel variable that is used to transfer state between a kernel and a circuit network.
 */
public sealed interface IOVariable : Variable<LogicState>, VariantVariable<LogicState> {

    override val type: KType
        get() = typeOf<LogicState>()
}

/**
 * A kernel scalar variable that is used to transfer state between a kernel and a circuit network.
 */
public sealed interface IOScalarVariable : ScalarVariable<LogicState>, IOVariable, InterfaceVariable

/**
 * A kernel vector variable that is used to transfer state between a kernel and a circuit network.
 */
public sealed interface IOVectorVariable : VectorVariable<LogicState>, IOVariable {

    public override fun get(index: Int): IOVectorVariableElement
}

/**
 * An element of an [IOVectorVariable].
 */
public sealed interface IOVectorVariableElement : VectorVariableElement<LogicState>, IOVariable, InterfaceVariable {

    override val vector: IOVectorVariable

    override val index: Int

    override val type: KType
        get() = super<VectorVariableElement>.type
}

/**
 * A [IOVariable] that can be assigned to a kernel interface like a component port or a circuit terminal.
 */
public sealed interface InterfaceVariable : IOVariable
