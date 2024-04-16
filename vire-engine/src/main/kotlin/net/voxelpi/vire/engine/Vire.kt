package net.voxelpi.vire.engine

public object Vire {

    /**
     * The version of the vire engine.
     */
    public val version: String
        get() = VireBuildParameters.VERSION

    /**
     * The exact version of the vire engine.
     */
    public val exactVersion: String
        get() = "${VireBuildParameters.VERSION}-${VireBuildParameters.GIT_COMMIT}"
}
