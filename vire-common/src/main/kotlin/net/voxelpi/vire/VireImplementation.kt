package net.voxelpi.vire

import net.voxelpi.vire.api.Vire

class VireImplementation : Vire {

    override val version: String
        get() = VireBuildParameters.VERSION

    override val longVersion: String
        get() = "${VireBuildParameters.VERSION}-${VireBuildParameters.GIT_COMMIT}"
}
