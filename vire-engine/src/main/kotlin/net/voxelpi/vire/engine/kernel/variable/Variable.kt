package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.LogicState
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * An abstract kernel variable.
 */
public sealed interface Variable<T> {

    /**
     * The name of the kernel variable.
     */
    public val name: String

    /**
     * The type of the kernel variable.
     */
    public val type: KType
}

/**
 * An abstract io variable.
 */
public sealed interface IOVector : Variable<Array<LogicState>> {

    /**
     * The initial size of the io vector.
     */
    public val initialSize: VariableInitialization<Int>

    override val type: KType
        get() = typeOf<Array<LogicState>>()
}

/**
 * An abstract io variable.
 */
public data class IOVectorElement(
    val vector: IOVector,
    val index: Int,
) {

    /**
     * The name of the variable. Consists of the name of the vector name and the element index.
     */
    public val name: String
        get() = "${vector.name}[$index]"
}
