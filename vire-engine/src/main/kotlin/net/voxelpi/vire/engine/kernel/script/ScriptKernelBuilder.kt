package net.voxelpi.vire.engine.kernel.script

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.Variable

public interface ScriptKernelBuilder {

    public val id: Identifier

    public val tags: MutableSet<Identifier>

    public val properties: MutableMap<Identifier, String>

    /**
     * The configuration action of the kernel.
     */
    public var configure: (ConfigurationContext) -> Unit

    /**
     * The initialization action of the kernel.
     */
    public var initialize: (InitializationContext) -> Unit

    /**
     * The update action of the kernel.
     */
    public var update: (UpdateContext) -> Unit

    /**
     * Declares the given [variable] on the kernel.
     */
    public fun <V : Variable<*>> declare(variable: V): V
}

internal class ScriptKernelBuilderImpl(
    override val id: Identifier,
) : ScriptKernelBuilder {

    override val tags: MutableSet<Identifier> = mutableSetOf()

    override val properties: MutableMap<Identifier, String> = mutableMapOf()

    override var configure: (ConfigurationContext) -> Unit = {}

    override var initialize: (InitializationContext) -> Unit = {}

    override var update: (UpdateContext) -> Unit = {}

    private val variables: MutableMap<String, Variable<*>> = mutableMapOf()

    private var finished: Boolean = false

    override fun <V : Variable<*>> declare(variable: V): V {
        check(!finished) { "Can't register a variable on a kernel after it has been build." }
        require(variable.name !in variables) { "A variable with the name \"${variable.name}\" already exists" }
        variables[variable.name] = variable
        return variable
    }

    fun build(): ScriptKernelImpl {
        finished = true
        return ScriptKernelImpl(
            id,
            tags.toSet(),
            properties.toMap(),
            variables.toMap(),
            configure,
            initialize,
            update,
        )
    }
}
