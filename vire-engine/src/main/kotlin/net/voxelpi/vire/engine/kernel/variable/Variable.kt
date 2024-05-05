package net.voxelpi.vire.engine.kernel.variable

import kotlin.reflect.KType

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

public sealed interface ScalarVariable<T> : Variable<T>

public sealed interface VectorVariable<T> : Variable<T> {

    public val size: VectorVariableSize

    public operator fun get(index: Int): VectorVariableElement<T>
}

public sealed interface VectorVariableElement<T> : Variable<T> {

    public val vector: VectorVariable<T>

    public val index: Int

    override val name: String
        get() = "${vector.name}[$index]"

    override val type: KType
        get() = vector.type
}

/**
 * The size of a vector variable.
 */
public sealed interface VectorVariableSize {

    /**
     * Returns the default size of the vector under the given parameter state.
     * Note that a dynamic vector size can be changed during the configuration, the returned value in this case is only the default value.
     */
    public fun get(state: ParameterStateProvider): Int

    /**
     * The size of the vector variable is set to the provided value.
     *
     * @param value the size of the vector variable.
     */
    public data class Value(val value: Int) : VectorVariableSize {

        override fun get(state: ParameterStateProvider): Int = value
    }

    /**
     * The size of the vector variable is set to the value of a parameter during configuration.
     *
     * @param parameter the parameter that should be used.
     */
    public data class Parameter(val parameter: net.voxelpi.vire.engine.kernel.variable.Parameter<Int>) : VectorVariableSize {

        override fun get(state: ParameterStateProvider): Int = state[parameter]
    }
}
