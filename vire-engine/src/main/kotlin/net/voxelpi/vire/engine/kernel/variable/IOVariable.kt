package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.LogicState
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public sealed interface IOVariable : Variable<LogicState> {

    override val type: KType
        get() = typeOf<LogicState>()
}

public sealed interface IOScalarVariable : ScalarVariable<LogicState>, IOVariable, InterfaceVariable

public sealed interface IOVectorVariable : VectorVariable<LogicState>, IOVariable {

    public override fun get(index: Int): IOVectorVariableElement
}

public sealed interface IOVectorVariableElement : VectorVariableElement<LogicState>, IOVariable, InterfaceVariable {

    override val vector: IOVectorVariable

    override val index: Int

    override val type: KType
        get() = super<VectorVariableElement>.type
}

public sealed interface InterfaceVariable
