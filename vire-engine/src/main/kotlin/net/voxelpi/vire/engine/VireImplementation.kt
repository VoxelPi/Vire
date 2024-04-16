package net.voxelpi.vire.engine

import net.voxelpi.vire.api.Vire
import net.voxelpi.vire.api.circuit.library.Library
import net.voxelpi.vire.engine.circuit.statemachine.VireStateMachineFactory
import net.voxelpi.vire.engine.environment.VireEnvironment

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

    override fun createEnvironment(libraries: List<Library>): VireEnvironment {
        return VireEnvironment(libraries)
    }
}
