package net.voxelpi.vire.engine.kernel.kotlin

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.variable.Variable

public interface ScriptKernelBuilder {

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

internal class ScriptKernelBuilderImpl : ScriptKernelBuilder {

    override val tags: MutableSet<Identifier> = mutableSetOf()

    override val properties: MutableMap<Identifier, String> = mutableMapOf()

    private var configurationAction: (ConfigurationContext) -> Unit = {}

    private var initializationAction: (InitializationContext) -> Unit = {}

    private var updateAction: (UpdateContext) -> Unit = {}

    private val variables: MutableMap<String, Variable<*>> = mutableMapOf()

    private var finished: Boolean = false

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

    fun build(): KotlinKernelImpl {
        finished = true
        return KotlinKernelImpl(
            tags.toSet(),
            properties.toMap(),
            variables.toMap(),
            configurationAction,
            initializationAction,
            updateAction,
        )
    }
}
