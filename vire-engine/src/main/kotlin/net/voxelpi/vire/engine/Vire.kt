package net.voxelpi.vire.engine

import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.environment.EnvironmentImpl

/**
 * The vire engine.
 */
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

    /**
     * Creates a new environment.
     */
    public fun createEnvironment(): Environment {
        return createEnvironmentImpl()
    }

    internal fun createEnvironmentImpl(): EnvironmentImpl {
        return EnvironmentImpl()
    }
}
