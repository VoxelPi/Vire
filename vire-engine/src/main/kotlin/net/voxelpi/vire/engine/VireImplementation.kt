package net.voxelpi.vire.engine

import net.voxelpi.vire.api.Vire

class VireImplementation : Vire {

    override val brand: String = "Vire Engine"

    override val version: String
        get() = VireBuildParameters.VERSION

    override val longVersion: String
        get() = "${VireBuildParameters.VERSION}-${VireBuildParameters.GIT_COMMIT}"
}
