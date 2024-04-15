package net.voxelpi.vire.engine

import net.voxelpi.vire.api.Vire
import net.voxelpi.vire.api.simulation.library.Library
import net.voxelpi.vire.engine.simulation.VireSimulation
import net.voxelpi.vire.engine.simulation.statemachine.VireStateMachineFactory

object VireImplementation : Vire {

    init {
        Vire.register(this)
    }

    override val brand: String = "Vire Engine"

    override val version: String
        get() = VireBuildParameters.VERSION

    override val longVersion: String
        get() = "${VireBuildParameters.VERSION}-${VireBuildParameters.GIT_COMMIT}"

    override val stateMachineFactory: VireStateMachineFactory = VireStateMachineFactory()

    override fun createSimulation(libraries: List<Library>): VireSimulation {
        return VireSimulation(libraries)
    }
}
