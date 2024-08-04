package net.voxelpi.vire.engine.kernel.procedual

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.Variable

public interface ProceduralKernelBuilder {

    public val tags: MutableSet<Identifier>

    public val properties: MutableMap<Identifier, String>

    /**
     * The configuration action of the kernel.
     */
    public fun onConfiguration(action: (ConfigurationContext) -> Unit)

    /**
     * The initialization action of the kernel.
     */
    public fun onInitialization(action: (InitializationContext) -> Unit)

    /**
     * The update action of the kernel.
     */
    public fun onUpdate(action: (UpdateContext) -> Unit)

    /**
     * Declares the given [variable] on the kernel.
     */
    public fun <V : Variable<*>> declare(variable: V): V
}

internal open class ProceduralKernelBuilderImpl : ProceduralKernelBuilder {

    override val tags: MutableSet<Identifier> = mutableSetOf()

    override val properties: MutableMap<Identifier, String> = mutableMapOf()

    protected var configurationAction: (ConfigurationContext) -> Unit = {}

    protected var initializationAction: (InitializationContext) -> Unit = {}

    protected var updateAction: (UpdateContext) -> Unit = {}

    protected val variables: MutableMap<String, Variable<*>> = mutableMapOf()

    protected var finished: Boolean = false

    override fun onConfiguration(action: (ConfigurationContext) -> Unit) {
        check(!finished) { "Can't modify the configuration action an of already build kernel." }
        configurationAction = action
    }

    override fun onInitialization(action: (InitializationContext) -> Unit) {
        check(!finished) { "Can't modify the initialization action an of already build kernel." }
        initializationAction = action
    }

    override fun onUpdate(action: (UpdateContext) -> Unit) {
        check(!finished) { "Can't modify the update action of an already build kernel." }
        updateAction = action
    }

    override fun <V : Variable<*>> declare(variable: V): V {
        check(!finished) { "Can't register a variable on a kernel after it has been build." }
        require(variable.name !in variables) { "A variable with the name \"${variable.name}\" already exists" }
        variables[variable.name] = variable
        return variable
    }

    open fun build(): ProceduralKernelImpl {
        finished = true
        return ProceduralKernelImpl(
            tags.toSet(),
            properties.toMap(),
            variables.toMap(),
            configurationAction,
            initializationAction,
            updateAction,
        )
    }
}
