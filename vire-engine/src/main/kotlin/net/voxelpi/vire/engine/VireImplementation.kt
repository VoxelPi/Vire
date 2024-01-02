package net.voxelpi.vire.engine

import net.voxelpi.vire.api.Vire
import net.voxelpi.vire.engine.simulation.statemachine.VireStateMachineFactory

class VireImplementation : Vire {

    override val brand: String = "Vire Engine"

    override val version: String
        get() = VireBuildParameters.VERSION

    override val longVersion: String
        get() = "${VireBuildParameters.VERSION}-${VireBuildParameters.GIT_COMMIT}"

    companion object {

        init {
            Vire.stateMachineFactory.register(VireStateMachineFactory())
        }
    }
}
