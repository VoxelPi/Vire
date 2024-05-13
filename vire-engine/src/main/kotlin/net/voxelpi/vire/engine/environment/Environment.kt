package net.voxelpi.vire.engine.environment

import net.voxelpi.event.EventScope
import net.voxelpi.event.eventScope
import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.environment.library.Library
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelInstance
import net.voxelpi.vire.engine.kernel.KernelInstanceImpl
import net.voxelpi.vire.engine.simulation.Simulation
import net.voxelpi.vire.engine.simulation.SimulationImpl

/**
 * A vire environment.
 */
public interface Environment {

    /**
     * The event scope of the environment.
     */
    public val eventScope: EventScope

    /**
     * All libraries that are registered in the environment.
     */
    public fun libraries(): Collection<Library>

    /**
     * Returns the library with the given [id].
     */
    public fun library(id: String): Library?

    /**
     * All kernels that are registered in the environment.
     */
    public fun kernels(): Collection<Kernel>

    /**
     * Returns the kernel with the given [id].
     */
    public fun kernel(id: Identifier): Kernel?

    /**
     * Creates a new circuit.
     */
    public fun createCircuit(id: Identifier): Circuit

    /**
     * Creates a new simulation.
     */
    public fun createSimulation(kernelInstance: KernelInstance): Simulation
}

internal class EnvironmentImpl(libraries: List<Library>) : Environment {

    override val eventScope: EventScope = eventScope()

    private val libraries = libraries.associateBy { it.id }
    private val kernels: Map<Identifier, Kernel> = libraries.map { it.kernels() }.flatten().associateBy { it.id }

    override fun libraries(): Collection<Library> {
        return libraries.values
    }

    override fun library(id: String): Library? {
        return libraries[id]
    }

    override fun kernels(): Collection<Kernel> {
        return kernels.values
    }

    override fun kernel(id: Identifier): Kernel? {
        return kernels[id]
    }

    override fun createCircuit(id: Identifier): Circuit {
        return CircuitImpl(this, id)
    }

    override fun createSimulation(kernelInstance: KernelInstance): Simulation {
        require(kernelInstance is KernelInstanceImpl)
        return SimulationImpl(this, kernelInstance)
    }
}
